# beam-pipelines-java

## Build for Flink

```shell
mvn package -Pflink-runner
```

## Run on Flink

submit `target/beam-pipelines-java-bundled-0.1.jar`

Entry class: `org.apache.beam.examples.WordCount`

Program Arguments: `--runner=FlinkRunner --output=counts`
