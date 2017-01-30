package com.kevinsimard.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

class Aggregator {

    private static final String KAFKA_HOSTS =
        Optional.of(System.getenv("KAFKA_HOSTS")).orElse("localhost:9092");

    private static final Serde<JsonNode> JSON_SERDE =
        Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer());

    private static List<String> processedMessages = new ArrayList<>();

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "aggregator");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOSTS);

        KafkaStreams streams = new KafkaStreams(buildStreams(), properties);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        streams.start();
    }

    private static KStreamBuilder buildStreams() {
        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, JsonNode> salesAggregated =
            builder.stream(Serdes.String(), JSON_SERDE, "sales-aggregated");

        salesAggregated.foreach(Aggregator::markAsProcessedInDatastore);

        KStream<String, JsonNode> salesRaw =
            builder.stream(Serdes.String(), JSON_SERDE, "sales-raw");

        salesRaw.filterNot(Aggregator::isAlreadyProcessed)
            .groupBy(Aggregator::groupByUserId, Serdes.String(), JSON_SERDE)
            .aggregate(() -> null, Aggregator::aggregateValues, JSON_SERDE, "aggregated-interm")
            .to(Serdes.String(), JSON_SERDE, "sales-aggregated");

        return builder;
    }

    @SuppressWarnings("unused")
    private static void markAsProcessedInCache(String key, JsonNode value) {
        processedMessages.add(uniqueHashForMessage(value));
    }

    @SuppressWarnings("unused")
    private static void markAsProcessedInDatastore(String key, JsonNode value) {
        // TODO: Save the generated hash in datastore.

        // TODO: Uncomment when using datastore to prevent array from getting too big.
        //processedMessages.remove(uniqueHashForMessage(value));
    }

    private static Boolean isAlreadyProcessed(String key, JsonNode value) {
        // TODO: Also check in datastore if not found in cache.

        Boolean isProcessed = processedMessages.indexOf(uniqueHashForMessage(value)) != -1;

        if (! isProcessed) markAsProcessedInCache(key, value);

        return isProcessed;
    }

    @SuppressWarnings("unused")
    private static String groupByUserId(String key, JsonNode value) {
        return value.get("user_id").asText();
    }

    @SuppressWarnings("unused")
    private static JsonNode aggregateValues(String key, JsonNode value, JsonNode previous) {
        if (previous != null) {
            ((ObjectNode) value).put("total", previous.get("total").asDouble() + value.get("total").asDouble());
        }

        return value;
    }

    private static String uniqueHashForMessage(JsonNode value) {
        // TODO: Replace the following with a hash function.
        return value.get("user_id").asText() + ":" + value.get("sale_id").asText();
    }
}
