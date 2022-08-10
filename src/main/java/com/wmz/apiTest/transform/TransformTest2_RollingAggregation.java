package com.wmz.apiTest.transform;

import com.wmz.apiTest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TransformTest2_RollingAggregation {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt");

        DataStream<SensorReading> ds = input.map(line -> {
            String[] splits = line.split(",");
            return new SensorReading(splits[0], new Long(splits[1]), new Double(splits[2]));
        });

        KeyedStream<SensorReading, String> keyedStream = ds.keyBy(new KeySelector<SensorReading, String>() {
            @Override
            public String getKey(SensorReading sensorReading) throws Exception {
                return sensorReading.getId();
            }
        });
//        ds.

//        KeyedStream<SensorReading, String> keyedStream1 = ds.keyBy(SensorReading::getId);

        keyedStream.max("temperature").print();

//        keyedStream.red

        env.execute();
    }
}
