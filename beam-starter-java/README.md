# Apache Beam starter for Java

Original repo

[GitHub - apache/beam-starter-java: Apache Beam starter repo for Java](https://github.com/apache/beam-starter-java)

## Run on Direct runner

```shell
./gradlew run --args=--inputText="🎉"
```

To build a self-contained jar file, we need to configure the [`jar`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html) task in the [`build.gradle`](build.gradle) file.

```shell
# Build a self-contained jar.
gradle jar

# Run the jar application.
java -jar build/pipeline.jar --inputText="🎉"
```

## Run on Flink

submit `build/pipeline.jar`

Entry class: `com.example.App`

Program Arguments: `--runner=FlinkRunner --inputText="🎉"`

## Switch main class

Change the build.gradle settings as below

```groovy
application {
    mainClass = 'com.example.WordCount'
}
```

## WordCount

```shell
./gradlew run --args=--output="./out/result"
```

## DebuggingWordCount

```shell
./gradlew run --args=--input="./README.md"
./gradlew run --args=--output="./out/result-d"
```

## JdbcIOSample

```shell
psql -U postgres

postgres=# create database bmdb;

psql -U postgres -d bmdb;
bmdb=#
```

```sql
DROP SCHEMA IF EXISTS bm CASCADE;
CREATE SCHEMA IF NOT EXISTS bm;

DROP TABLE IF EXISTS bm.user;
CREATE TABLE bm.user(
  user_id character(6) NOT NULL,
  user_name character varying(20),
  user_email character varying(30),
  PRIMARY KEY(user_id)
);

INSERT INTO bm.user (user_id, user_name, user_email) VALUES ('U00001', 'Bob', 'bob@example.com');
INSERT INTO bm.user (user_id, user_name, user_email) VALUES ('U00002', 'Alice', 'alice@example.com');
INSERT INTO bm.user (user_id, user_name, user_email) VALUES ('U00003', 'Carol', 'carol@example.com');
```

Input 50,000 data items

```sql
INSERT INTO bm.user (user_id, user_name, user_email)
SELECT 
    'U' || LPAD(CAST(id AS TEXT), 5, '0'),
    substr(md5(random()::text), 1, 10),
    substr(md5(random()::text), 1, 10) || '@example.com'
FROM generate_series(1, 50000) AS id;
```

## KafkaIOSample

Create topics

```shell
kafka-topics --create --bootstrap-server=localhost:9092 --replication-factor 1 --partitions 1 --topic beam-topic
kafka-topics --create --bootstrap-server=localhost:9092 --replication-factor 1 --partitions 1 --topic beam-topic-out
```

Run on flink

```shell
./bin/flink run --detached -Dexecution.checkpointing.interval='3s' -Dstate.checkpoint-storage='filesystem' -Dstate.checkpoints.dir='file:///Users/kondoh/dev/flink-1.16.2/checkpoints/' ../beam-study/beam-starter-java/build/pipeline.jar --runner=FlinkRunner
```


Send message
```shell
kcat -b localhost:9092 -t beam-topic -P
foo
bar
baz
foo
bar
^d
```

Receive message

```shell
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic beam-topic-out --from-beginning
```
