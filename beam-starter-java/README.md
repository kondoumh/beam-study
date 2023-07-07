# Apache Beam starter for Java

Original repo

[GitHub - apache/beam-starter-java: Apache Beam starter repo for Java](https://github.com/apache/beam-starter-java)

## Run on Direct runner

```shell
./gradlew run --args=--inputText="🎉"
```

To build a self-contained jar file, we need to configure the [`jar`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html) task in the [`build.gradle`](build.gradle) file.

```sh
# Build a self-contained jar.
gradle jar

# Run the jar application.
java -jar build/pipeline.jar --inputText="🎉"
```

## Run on Flink

Entry class: `com.example.App`

Program Arguments: `--runner=FlinkRunner --inputText="🎉" `
