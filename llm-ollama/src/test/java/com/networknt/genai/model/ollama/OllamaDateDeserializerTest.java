package com.networknt.genai.model.ollama;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class OllamaDateDeserializerTest {

    @Test
    void should_trim_nanoseconds_and_deserialize_utc_date() throws IOException {
        JsonParser jsonParser = new JsonParserDelegate(null) {
            @Override
            public String getText() throws IOException {
                return "2024-09-04T15:21:17.521503059Z";
            }
        };

        OffsetDateTime offsetDateTime = new OllamaDateDeserializer().deserialize(jsonParser, null);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.UTC);
        assertThat(offsetDateTime.getYear()).isEqualTo(2024);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(9);
        assertThat(offsetDateTime.getHour()).isEqualTo(15);
        assertThat(offsetDateTime.getNano()).isEqualTo(0);
    }

    @Test
    void should_trim_nanoseconds_and_deserialize_utc_date_with_offset() throws IOException {
        JsonParser jsonParser = new JsonParserDelegate(null) {
            @Override
            public String getText() throws IOException {
                return "2024-08-04T00:54:54.764563036+02:00";
            }
        };

        OffsetDateTime offsetDateTime = new OllamaDateDeserializer().deserialize(jsonParser, null);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(2));
        assertThat(offsetDateTime.getYear()).isEqualTo(2024);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(8);
        assertThat(offsetDateTime.getHour()).isEqualTo(0);
        assertThat(offsetDateTime.getNano()).isEqualTo(0);
    }


    @Test
    void should_trim_nanoseconds_and_deserialize_utc_date_with_negative_offset() throws IOException {
        JsonParser jsonParser = new JsonParserDelegate(null) {
            @Override
            public String getText() throws IOException {
                return "2024-06-15T05:18:13.974383393-07:00";
            }
        };

        OffsetDateTime offsetDateTime = new OllamaDateDeserializer().deserialize(jsonParser, null);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(-7));
        assertThat(offsetDateTime.getYear()).isEqualTo(2024);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(6);
        assertThat(offsetDateTime.getHour()).isEqualTo(5);
        assertThat(offsetDateTime.getNano()).isEqualTo(0);
    }
}
