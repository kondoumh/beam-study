# beam-pipelines-java

## Build for Flink

```shell
mvn package -Pflink-runner
```

## Run on Flink

submit `target/beam-pipelines-java-bundled-0.1.jar`

### WordCount

Entry class: `org.apache.beam.examples.WordCount`

Program Arguments: `--runner=FlinkRunner --output=counts`

### KafkaWordCountJSON

Entry class: `org.apache.beam.examples.KafkaWordCountJson`

Program Arguments:
