package com.wmz.apiTest.source;

import com.wmz.apiTest.beans.SensorReading;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;


import java.util.Arrays;

public class SourceTest1_Collection {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<SensorReading> dataStream = env.fromCollection(Arrays.asList(
                new SensorReading("1", 1547718201L, 17.4),
                new SensorReading("1", 1563318201L, 16.4),
                new SensorReading("1", 1000L, 17.4),
                new SensorReading("2", 1547763201L, 19.4),
                new SensorReading("2", 1547718236L, 18.4)
        ));

        SingleOutputStreamOperator<Tuple2<SensorReading, String>> process = dataStream.process(new ProcessFunction<SensorReading, Tuple2<SensorReading, String>>() {
            @Override
            public void processElement(SensorReading value, ProcessFunction<SensorReading, Tuple2<SensorReading, String>>.Context ctx, Collector<Tuple2<SensorReading, String>> out) throws Exception {
                out.collect(Tuple2.of(value, value.getId()));
            }
        });

        process.keyBy(value -> value.f0.getId()).maxBy("temperature").print();

//        dataStream.print("sensorReading");

//        dataStream.keyBy(SensorReading::getId).maxBy("temperature").print();


        env.execute("sensorReadingJob");

    }
}
