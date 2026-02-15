package com.networknt.genai.internal;

import com.networknt.genai.Internal;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Internal
public class Markers {

    private Markers() {}

    public static final Marker SENSITIVE = MarkerFactory.getMarker("SENSITIVE");
}
