# Kafka Streams Aggregation Example

## Code Structure

    ├── src
    │   └── main
    │       ├── java
    │       │   └── com
    │       │       └── kevinsimard
    │       │           └── stream
    │       │               └── Aggregator.java
    │       └── resources
    │           └── log4j.properties
    ├── .editorconfig
    ├── .gitattributes
    ├── .gitignore
    ├── LICENSE.txt
    ├── pom.xml
    └── README.md

## Installation

Run `$ mvn package` to build the JAR file with the dependencies.

Create the required Kafka topics with the following commands.

```
$ kafka-topics --zookeeper localhost --create --partitions 4 --replication-factor 1 --topic sales-raw
$ kafka-topics --zookeeper localhost --create --partitions 4 --replication-factor 1 --topic sales-aggregated
$ kafka-topics --zookeeper localhost --create --partitions 4 --replication-factor 1 --topic stream-aggregated-interm-repartition
$ kafka-topics --zookeeper localhost --create --partitions 4 --replication-factor 1 --topic stream-aggregated-interm-changelog
```

Run `$ java -jar ./target/kafka-streams-aggregation-0.1.0-jar-with-dependencies.jar` to run the application.

Use the Kafka console producer tool to test the application.

```
$ kafka-console-producer --broker-list localhost:9092 --topic sales-raw
```

Use the Kafka console consumer tool to view aggregated messages.

```
$ kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic sales-aggregated
```

Enter the following message to start aggregating numbers.

```bash
# Format: {"user_id":<int>,"sale_id":<int>,"total":<double>}
{"user_id":1,"sale_id":1,"total":100.00}
```

## License

This package is open-sourced software licensed under the [MIT license](http://opensource.org/licenses/MIT).
