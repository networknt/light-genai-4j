package com.networknt.agent.data.document.parser;

import com.networknt.agent.data.document.BlankDocumentException;
import com.networknt.agent.data.document.Document;
import com.networknt.agent.data.document.DocumentParser;

import java.io.InputStream;
import java.nio.charset.Charset;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TextDocumentParser implements DocumentParser {

    private final Charset charset;

    public TextDocumentParser() {
        this(UTF_8);
    }

    public TextDocumentParser(Charset charset) {
        this.charset = ensureNotNull(charset, "charset");
    }

    @Override
    public Document parse(InputStream inputStream) {
        try {
            String text = new String(inputStream.readAllBytes(), charset);
            if (text.isBlank()) {
                throw new BlankDocumentException();
            }
            return Document.from(text);
        } catch (BlankDocumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
