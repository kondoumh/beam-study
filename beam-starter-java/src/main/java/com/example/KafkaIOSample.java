package com.example;

import java.util.Map;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

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
        .withMaxNumRecords(5)
        .withoutMetadata())
      .apply(Values.create())
      .apply("ExtractWords", 
        ParDo.of(
          new DoFn<String, String>() {
            @ProcessElement
            public void processElement(ProcessContext c) {
              for (String word : c.element().split(TOKENIZER_PATTERN, 0)) {
                if (!word.isEmpty()) {
                  c.output(word);
                }
              }
            }
          }))
      .apply("CountWords", Count.perElement())
      .apply("FormatResults",
          MapElements.via(
            new SimpleFunction<KV<String, Long>, String>() {
              @Override
              public String apply(KV<String, Long> input) {
                return input.getKey() + ": " + input.getValue();
              }
            }))
      .apply("Write to text", TextIO.write().to("out/word-counts").withSuffix(".txt"));

      pipeline.run().waitUntilFinish();
  }
}
