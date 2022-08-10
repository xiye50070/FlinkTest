package com.wmz.apiTest.source;

import com.wmz.apiTest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

public class SourceTest1_Collection {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<SensorReading> dataStream = env.fromCollection(Arrays.asList(
                new SensorReading("1", 1547718201L, 15.4),
                new SensorReading("2", 1563318201L, 16.4),
                new SensorReading("3", 1547763201L, 17.4),
                new SensorReading("4", 1547718236L, 18.4)
        )).setParallelism(1);
        dataStream.print("sensorReading");

        env.execute("sensorReadingJob");

    }
}
