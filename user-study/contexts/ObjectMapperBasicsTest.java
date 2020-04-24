package com.thepracticaldeveloper.objectmapperbasics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.thepracticaldeveloper.objectmapperbasics.samplebeans.*;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ObjectMapperBasicsTest {

  private final static Logger log =
    LoggerFactory.getLogger(ObjectMapperBasicsTest.class);

  @Test
  public void serializeSimpleString() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var personName = "Juan Garcia";
    var json = mapper.writeValueAsString(personName);
    log.info("Serializing a plain String: {}", json);
    assertThat(json).isEqualTo("\"Juan Garcia\"");
  }

  @Test
  public void serializeStringAsObject() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var stringWrapperSerializer = new StdSerializer<String>(String.class) {
      @Override
      public void serialize(String s,
                            JsonGenerator jsonGenerator,
                            SerializerProvider serializerProvider)
        throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("string", s);
        jsonGenerator.writeEndObject();
      }
    };
    mapper.registerModule(new SimpleModule()
      .addSerializer(stringWrapperSerializer));

    var personName = "Juan Garcia";
    var json = mapper.writeValueAsString(personName);
    log.info("Using a custom serializer (in this case for a String): {}", json);
    assertThat(json).isEqualTo(
      "{\"string\":\"Juan Garcia\"}"
    );
  }

  @Test
  public void serializeListOfString() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var personNames = List.of("Juan Garcia", "Manuel Perez");
    var json = mapper.writeValueAsString(personNames);
    log.info("A simple list of String objects looks like this: {}", json);
    assertThat(json).isEqualTo(
      "[\"Juan Garcia\",\"Manuel Perez\"]"
    );
  }

  @Test
  public void serializeMapOfString() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var personNames = new TreeMap<String, String>();
    personNames.put("name1", "Juan Garcia");
    personNames.put("name2", "Manuel Perez");
    var json = mapper.writeValueAsString(personNames);
    log.info("A simple map of <String, String>: {}", json);
    assertThat(json).isEqualTo(
      "{\"name1\":\"Juan Garcia\",\"name2\":\"Manuel Perez\"}"
    );
  }

  @Test
  public void serializeListOfPersonName() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var personNames = List.of(
      new PersonName("Juan Garcia"),
      new PersonName("Manuel Perez")
    );
    var json = mapper.writeValueAsString(personNames);
    log.info("A list of simple PersonName objects converted to JSON: {}", json);
    assertThat(json).isEqualTo(
      "[{\"name\":\"Juan Garcia\"},{\"name\":\"Manuel Perez\"}]"
    );
  }

  @Test
  public void serializeListOfPerson() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    var personNames = List.of(
      new Person("Juan Garcia",
        LocalDate.of(1980, 9, 15)),
      new Person("Manuel Perez",
        LocalDate.of(1987, 7, 23))
    );
    var json = mapper.writeValueAsString(personNames);
    log.info("A list of simple Person objects converted to JSON: {}", json);
    // By default, Jackson serializes LocalDate and LocalDateTime exporting
    // all the object fields as with any other object. You need to use the
    // JavaTimeModule, although the default formatter is a bit weird (array).
    assertThat(json).isEqualTo(
      "[{\"name\":\"Juan Garcia\",\"birthdate\":[1980,9,15]}," +
        "{\"name\":\"Manuel Perez\",\"birthdate\":[1987,7,23]}]"
    );
  }

  @Test
  public void serializeListOfPersonFormatted() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    // You can use a custom module to change the formatter
    mapper.registerModule(new SimpleModule().addSerializer(
      new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE)));
    var personNames = List.of(
      new Person("Juan Garcia",
        LocalDate.of(1980, 9, 15)),
      new Person("Manuel Perez",
        LocalDate.of(1987, 7, 23))
    );
    var json = mapper.writeValueAsString(personNames);
    log.info("A list of simple Person objects converted to JSON: {}", json);
    // In this case you get the ISO format YYYY-MM-DD
    assertThat(json).isEqualTo(
      "[{\"name\":\"Juan Garcia\",\"birthdate\":\"1980-09-15\"}," +
        "{\"name\":\"Manuel Perez\",\"birthdate\":\"1987-07-23\"}]"
    );
  }

  @Test
  public void deserializeListOfString() throws IOException {
    var mapper = new ObjectMapper();
    var json = "[\"Juan Garcia\",\"Manuel Perez\"]";
    var list = mapper.readValue(json, List.class);
    log.info("Deserializing a list of plain Strings: {}", list);
    assertThat(list).containsExactly("Juan Garcia", "Manuel Perez");
  }

  @Test
  public void deserializeToListOfStringUsingCustomModule() throws IOException {
    var mapper = new ObjectMapper();

    var deserializer = new StdDeserializer<String>(String.class) {
      @Override
      public String deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        return ((TextNode) p.getCodec().readTree(p).get("string")).textValue();
      }
    };

    mapper.registerModule(
      new SimpleModule().addDeserializer(String.class, deserializer));

    var json = "[{\"string\":\"Juan Garcia\"},{\"string\":\"Manuel Perez\"}]";
    // You can use this option or you can deserialize to String[]
    var stringCollectionType = mapper.getTypeFactory()
      .constructCollectionType(List.class, String.class);
    List<String> values = mapper.readValue(json, stringCollectionType);
    log.info("Using a custom deserializer to extract field values: {}", values);
    assertThat(values).containsExactly("Juan Garcia", "Manuel Perez");
  }

  @Test
  public void deserializeListOfStringObjectsUsingTree() throws IOException {
    var mapper = new ObjectMapper();
    var json = "[{\"string\":\"Juan Garcia\"},{\"string\":\"Manuel Perez\"}]";
    var values = mapper.readTree(json).findValuesAsText("string");
    log.info("Using the JSON tree reader to extract field values: {}", values);
    assertThat(values).containsExactly("Juan Garcia", "Manuel Perez");
  }
}