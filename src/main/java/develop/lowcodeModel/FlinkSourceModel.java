package develop.lowcodeModel;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

public class FlinkSourceModel {






    public DataStream<String> fileSource(StreamExecutionEnvironment env,String filePath){
        DataStream<String> ds = env.readTextFile(filePath);
        return ds;
    }


    public DataStream<String> kafkaSouarce(StreamExecutionEnvironment env, String bootStrapServers, List<String> topics, String groupId){
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setProperty("partition.discovery.interval.ms", "60000")
                .setBootstrapServers(bootStrapServers)
                .setTopics(topics)
                .setGroupId(groupId)
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.latest())
                .build();
        DataStreamSource<String> ds = env.fromSource(source, WatermarkStrategy.noWatermarks(), "DevelopTest");
        return ds;
    }
}
