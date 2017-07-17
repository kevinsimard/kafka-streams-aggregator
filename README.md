# Kafka Streams Aggregator

## Code Structure

    ├── src
    │   └── main
    │       └── java
    │           └── com
    │               └── kevinsimard
    │                   └── kafka
    │                       └── streams
    │                           └── Aggregator.java
    ├── .editorconfig
    ├── .gitattributes
    ├── .gitignore
    ├── LICENSE.md
    ├── README.md
    └── pom.xml

## Usage

Create Kafka topics with the following commands.

```
$ kafka-topics --zookeeper localhost --create --partitions 1 --replication-factor 1 --topic sales-raw
$ kafka-topics --zookeeper localhost --create --partitions 1 --replication-factor 1 --topic sales-aggregated
```

Run `$ java -jar target/kafka-streams-aggregator-1.0.0.jar` to run the application.

Use the Kafka console producer tool to test the application.

```
$ kafka-console-producer --broker-list localhost:9092 --topic sales-raw
```

Enter the following message to start aggregating numbers.

```bash
# Format: {"user_id":<int>,"sale_id":<int>,"total":<double>}
{"user_id":1,"sale_id":1,"total":100.00}
```

Use the Kafka console consumer tool to view aggregated messages.

```
$ kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic sales-aggregated
```

## License

This package is open-sourced software licensed under the [MIT license](http://opensource.org/licenses/MIT).
