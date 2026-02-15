package com.networknt.genai.model.ollama;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class OllamaDateDeserializerTest {

    @Test
    void should_trim_nanoseconds_and_deserialize_utc_date() throws IOException {
        String json = "\"2024-09-04T15:21:17.521503059Z\"";
        JsonParser parser = new JsonFactory().createParser(json);
        parser.nextToken(); // Move to the string token

        OffsetDateTime offsetDateTime = new OllamaDateDeserializer().deserialize(parser, null);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.UTC);
        assertThat(offsetDateTime.getYear()).isEqualTo(2024);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(9);
        assertThat(offsetDateTime.getHour()).isEqualTo(15);
        assertThat(offsetDateTime.getNano()).isEqualTo(0);
    }

    @Test
    void should_trim_nanoseconds_and_deserialize_utc_date_with_offset() throws IOException {
        String json = "\"2024-08-04T00:54:54.764563036+02:00\"";
        JsonParser parser = new JsonFactory().createParser(json);
        parser.nextToken();

        OffsetDateTime offsetDateTime = new OllamaDateDeserializer().deserialize(parser, null);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(2));
        assertThat(offsetDateTime.getYear()).isEqualTo(2024);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(8);
        assertThat(offsetDateTime.getHour()).isEqualTo(0);
        assertThat(offsetDateTime.getNano()).isEqualTo(0);
    }

    @Test
    void should_trim_nanoseconds_and_deserialize_utc_date_with_negative_offset() throws IOException {
        String json = "\"2024-06-15T05:18:13.974383393-07:00\"";
        JsonParser parser = new JsonFactory().createParser(json);
        parser.nextToken();

        OffsetDateTime offsetDateTime = new OllamaDateDeserializer().deserialize(parser, null);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(-7));
        assertThat(offsetDateTime.getYear()).isEqualTo(2024);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(6);
        assertThat(offsetDateTime.getHour()).isEqualTo(5);
        assertThat(offsetDateTime.getNano()).isEqualTo(0);
    }
}