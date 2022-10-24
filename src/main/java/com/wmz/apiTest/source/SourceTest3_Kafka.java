package com.wmz.apiTest.source;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


import java.util.Properties;

public class SourceTest3_Kafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setProperty("partition.discovery.interval.ms", "60000")
                .setBootstrapServers("192.168.165.27:9092")
                .setTopics("DevelopTest")
                .setGroupId("1")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.latest())
                .build();



//        DataStreamSource<String> ds = env.addSource(new FlinkKafkaConsumer011<String>("DevelopTest", new SimpleStringSchema(), properties));
//        env.addSource(new FlinkKafkaConsumer<String>("DevelopTest", new SimpleStringSchema(), properties));
        DataStreamSource<String> ds = env.fromSource(source, WatermarkStrategy.noWatermarks(), "DevelopTest");

//        ds.keyBy()


        ds.print();
        env.execute();

    }
}
