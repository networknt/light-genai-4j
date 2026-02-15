package com.networknt.genai.invocation;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.tool.Tool;
import com.networknt.genai.rag.query.Metadata;
import com.networknt.genai.rag.query.Query;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents arbitrary parameters available during a single AI Service invocation.
 * {@code InvocationParameters} can be specified when invoking the AI Service:
 *
 * <pre>
 * interface Assistant {
 *     String chat(@UserMessage String userMessage, InvocationParameters parameters);
 * }
 *
 * InvocationParameters parameters = InvocationParameters.from(Map.of("userId", "12345"));
 * String response = assistant.chat("What is the weather in London?", parameters);
 * </pre>
 * <p>
 * {@code InvocationParameters} can be accessed within the {@link Tool}-annotated method:
 * <pre>
 * class Tools {
 *     <code>@Tool</code>
 *     String getWeather(String city, InvocationParameters parameters) {
 *         String userId = parameters.get("userId");
 *         UserPreferences preferences = getUserPreferences(userId);
 *         return weatherService.getWeather(city, preferences.temperatureUnits());
 *     }
 * }
 * </pre>
 * <p>
 * In this case, the LLM is not aware of these parameters; they are only visible to LangChain4j and user code.
 * <p>
 * {@code InvocationParameters} can also be accessed within other AI Service components, such as:
 * <pre>
 * - ToolProvider: inside the ToolProviderRequest
 * - ToolArgumentsErrorHandler and ToolExecutionErrorHandler: inside the ToolErrorContext
 * - RAG components: inside the {@link Query} -> {@link Metadata}
 * </pre>
 * <p>
 * Parameters are stored in a mutable, thread safe {@link Map}.
 *
 * @since 1.6.0
 */
public class InvocationParameters {

    private final ConcurrentHashMap<String, Object> map;

    /**
     * Creates an empty instance.
     */
    public InvocationParameters() {
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new instance with the given map.
     *
     * @param map the map
     */
    public InvocationParameters(Map<String, Object> map) {
        ensureNotNull(map, "map");
        this.map = new ConcurrentHashMap<>(map);
    }

    /**
     * Returns the parameters as a map.
     *
     * @return the map
     */
    public Map<String, Object> asMap() {
        return map;
    }

    /**
     * Returns the value for the given key.
     *
     * @param key the key
     * @param <T> the type of the value
     * @return the value
     */
    public <T> T get(String key) {
        return (T) map.get(key);
    }

    /**
     * Returns the value for the given key, or the default value if the key is not present.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @param <T>          the type of the value
     * @return the value
     */
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) map.getOrDefault(key, defaultValue);
    }

    /**
     * Puts the given key-value pair into the parameters.
     *
     * @param key   the key
     * @param value the value
     * @param <T>   the type of the value
     */
    public <T> void put(String key, T value) {
        map.put(key, value);
    }

    /**
     * Returns true if the parameters contain the given key.
     *
     * @param key the key
     * @return true if the parameters contain the key
     */
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        InvocationParameters that = (InvocationParameters) object;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }

    @Override
    public String toString() {
        return "InvocationParameters{" + "map=" + map + '}';
    }

    /**
     * Creates a new instance with the given key-value pair.
     *
     * @param key   the key
     * @param value the value
     * @return the new instance
     */
    public static InvocationParameters from(String key, Object value) {
        return new InvocationParameters(Map.of(key, value));
    }

    /**
     * Creates a new instance with the given map.
     *
     * @param map the map
     * @return the new instance
     */
    public static InvocationParameters from(Map<String, Object> map) {
        return new InvocationParameters(map);
    }
}
