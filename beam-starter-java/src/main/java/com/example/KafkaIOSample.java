package com.example;

import java.util.Map;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaIOSample {
  static final String TOKENIZER_PATTERN = "[^\\p{L}]+";

  public interface Options extends PipelineOptions {
  }

  public static class FormatAsTextFn extends SimpleFunction<KV<String, Long>, String> {
    @Override
    public String apply(KV<String, Long> input) {
      return input.getKey() + ": " + input.getValue();
    }
  }

  public static void main(String[] args) {
    PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
    Pipeline pipeline = Pipeline.create(options);

    pipeline
      .apply("Read from Kafka", KafkaIO.<Long, String>read()
        .withBootstrapServers("localhost:9092")
        .withTopic("beam-topic")
        .withKeyDeserializer(LongDeserializer.class)
        .withValueDeserializer(StringDeserializer.class)
        .withConsumerConfigUpdates(Map.ofEntries(
          Map.entry("auto.offset.reset", "earliest"),
          Map.entry("enable.auto.commit", true),
          Map.entry("group.id", "beam-group"))
        )
        .withoutMetadata())
      .apply("WriteToKafka",
      KafkaIO.<Long, String> write()
              .withBootstrapServers(
                      "localhost:9092")
              .withTopic("beam-topic-out")
              .withKeySerializer(
                      org.apache.kafka.common.serialization.LongSerializer.class)
              .withValueSerializer(
                      org.apache.kafka.common.serialization.StringSerializer.class));      

      pipeline.run().waitUntilFinish();
  }
}
