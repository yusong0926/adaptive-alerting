package com.expedia.adaptivealerting.kafka;

import com.expedia.metrics.MetricData;
import com.expedia.metrics.MetricDefinition;
import com.expedia.metrics.TagCollection;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaTest {

    public static void main(String[] args) {


        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "com.expedia.adaptivealerting.kafka.serde.MetricDataSerde$MetricDataSerializer");

        Producer<String, MetricData> producer = new KafkaProducer<>(props);

        MetricDefinition metricDefinition = new MetricDefinition(
                "foo",
                new TagCollection(
                        ImmutableMap.<String, String>builder()
                                .put("mtype", "mtype")
                                .put("unit", "unit")
                                .put("org_id", "1")
                                .put("interval", "1")
                                .build()
                ),
                TagCollection.EMPTY
        );
        MetricData metricData = new MetricData(metricDefinition, 1.0, 0);

        producer.send(new ProducerRecord<>("aa-metrics", null, metricData));
        producer.flush();

    }
}
