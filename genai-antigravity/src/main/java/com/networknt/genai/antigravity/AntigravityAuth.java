package com.networknt.genai.antigravity;

import com.networknt.client.Http2Client;
import com.networknt.config.Config;
import io.undertow.Undertow;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.OptionMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AntigravityAuth {
    private static final Logger logger = LoggerFactory.getLogger(AntigravityAuth.class);
    private static final AntigravityConfig config = AntigravityConfig.load();
    private static final Http2Client client = Http2Client.getInstance();

    private static String accessToken;
    private static String refreshToken;
    private static long tokenExpiry;

    public synchronized String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) {
            return accessToken;
        }
        if (refreshToken != null) {
            // Refresh logic (omitted for brevity, assume full re-auth for now or implement if easy)
            // Ideally we should implement refresh flow here.
        }
        return authorize();
    }

    private String authorize() {
        try {
            String verifier = generateVerifier();
            String challenge = generateChallenge(verifier);
            String state = generateState();
            
            String redirectUri = config.getRedirectUri();
            URI uri = new URI(redirectUri);
            int port = uri.getPort() > 0 ? uri.getPort() : 51121;
            
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<String> codeRef = new AtomicReference<>();
            AtomicReference<String> stateRef = new AtomicReference<>();

            Undertow server = Undertow.builder()
                    .addHttpListener(port, "localhost")
                    .setHandler(new HttpHandler() {
                        @Override
                        public void handleRequest(HttpServerExchange exchange) throws Exception {
                            if (exchange.isInIoThread()) {
                                exchange.dispatch(this);
                                return;
                            }
                            String code = exchange.getQueryParameters().get("code").getFirst();
                            String returnedState = exchange.getQueryParameters().get("state").getFirst();
                            
                            codeRef.set(code);
                            stateRef.set(returnedState);
                            
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                            exchange.getResponseSender().send("<h1>Authentication complete</h1><p>You can close this window and return to the application.</p>");
                            latch.countDown();
                        }
                    })
                    .build();
            server.start();

            String authUrl = buildAuthUrl(challenge, state, redirectUri);
            System.out.println("Please open the following URL in your browser to authenticate:");
            System.out.println(authUrl);
            logger.info("Please open the following URL in your browser to authenticate: " + authUrl);

            // Wait for callback
            if (latch.await(5, TimeUnit.MINUTES)) {
                server.stop();
                if (state.equals(stateRef.get())) {
                    return exchangeCodeForToken(codeRef.get(), verifier, redirectUri);
                } else {
                    logger.error("State mismatch in OAuth callback");
                    System.err.println("State mismatch in OAuth callback");
                }
            } else {
                server.stop();
                logger.error("Timeout waiting for OAuth callback");
                System.err.println("Timeout waiting for OAuth callback");
            }

        } catch (Exception e) {
            logger.error("Error during Antigravity authentication", e);
            e.printStackTrace();
        }
        return null;
    }
    
    private String exchangeCodeForToken(String code, String verifier, String redirectUri) throws Exception {
        URI tokenUri = new URI(config.getTokenUrl());
        ClientConnection connection = client.connect(tokenUri, Http2Client.WORKER, Http2Client.SSL, Http2Client.BUFFER_POOL, OptionMap.EMPTY).get();
        
        try {
            Map<String, String> params = new HashMap<>();
            params.put("client_id", config.getClientId());
            params.put("client_secret", config.getClientSecret());
            params.put("code", code);
            params.put("grant_type", "authorization_code");
            params.put("redirect_uri", redirectUri);
            params.put("code_verifier", verifier);
            
            String requestBody = buildFormData(params);
            
            ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(tokenUri.getPath());
            request.getRequestHeaders().put(Headers.HOST, "oauth2.googleapis.com");
            request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/x-www-form-urlencoded");
            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");

            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<ClientResponse> reference = new AtomicReference<>();
            
            connection.sendRequest(request, client.createClientCallback(reference, latch, requestBody));
            
            if (latch.await(10, TimeUnit.SECONDS)) {
                ClientResponse response = reference.get();
                if (response.getResponseCode() == 200) {
                    String responseBody = response.getAttachment(Http2Client.RESPONSE_BODY);
                    Map<String, Object> tokenData = Config.getInstance().getMapper().readValue(responseBody, Map.class);
                    
                    accessToken = (String) tokenData.get("access_token");
                    refreshToken = (String) tokenData.get("refresh_token");
                    Integer expiresIn = (Integer) tokenData.get("expires_in");
                    if(expiresIn != null) {
                        tokenExpiry = System.currentTimeMillis() + (expiresIn * 1000) - (60 * 1000); // Buffer of 1 minute
                    }
                    return accessToken;
                } else {
                     String responseBody = response.getAttachment(Http2Client.RESPONSE_BODY);
                     String errorMsg = "Failed to exchange token. Status: " + response.getResponseCode() + " Body: " + responseBody;
                     System.err.println(errorMsg);
                     logger.error(errorMsg);
                }
            } else {
                System.err.println("Timeout waiting for token exchange response.");
            }
        } finally {
            connection.close();
        }
        return null;
    }

    private String buildAuthUrl(String challenge, String state, String redirectUri) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(config.getAuthUrl());
        sb.append("?client_id=").append(URLEncoder.encode(config.getClientId(), "UTF-8"));
        sb.append("&response_type=code");
        sb.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, "UTF-8"));
        sb.append("&scope=").append(URLEncoder.encode("https://www.googleapis.com/auth/cloud-platform https://www.googleapis.com/auth/userinfo.email", "UTF-8"));
        sb.append("&code_challenge=").append(challenge);
        sb.append("&code_challenge_method=S256");
        sb.append("&state=").append(state);
        sb.append("&access_type=offline");
        sb.append("&prompt=consent");
        return sb.toString();
    }
    
    private String buildFormData(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) first = false;
            else result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private String generateVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    private String generateChallenge(String verifier) throws NoSuchAlgorithmException {
        byte[] bytes = verifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
    
    private String generateState() {
        SecureRandom sr = new SecureRandom();
        byte[] state = new byte[16];
        sr.nextBytes(state);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(state);
    }
}
