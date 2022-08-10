package com.wmz.apiTest.transform;

import com.wmz.apiTest.beans.SensorReading;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TransformTest3_Reduce {
    public static void main(String[] args) throws Exception {
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

        keyedStream.reduce((ReduceFunction<SensorReading>) (value1, value2) -> new SensorReading(value1.getId(),
                value2.getTimestamp(), Math.max(value1.getTemperature(), value1.getTemperature()))).print();


        env.execute();
    }


}
