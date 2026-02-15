package com.networknt.genai.invocation;

import java.util.Map;
import com.networknt.genai.Internal;

/**
 * A marker interface for components that are managed by LangChain4j framework.
 * <p>
 * Implementing this interface indicates that the component is internally managed by LangChain4j,
 * and doesn't require to be instatiated or passed around by the user or LLM.
 *
 * @since 1.8.0
 */
@Internal
public interface LangChain4jManaged {


    /**
     * The current managed components for the current thread.
     */
    ThreadLocal<Map<Class<? extends LangChain4jManaged>, LangChain4jManaged>> CURRENT = new ThreadLocal<>();

    /**
     * Sets the current managed components.
     *
     * @param current the managed components
     */
    static void setCurrent(Map<Class<? extends LangChain4jManaged>, LangChain4jManaged> current) {
        CURRENT.set(current);
    }

    /**
     * Returns the current managed components.
     *
     * @return the managed components
     */
    static Map<Class<? extends LangChain4jManaged>, LangChain4jManaged> current() {
        return CURRENT.get();
    }

    /**
     * Removes the current managed components.
     */
    static void removeCurrent() {
        CURRENT.remove();
    }
}
