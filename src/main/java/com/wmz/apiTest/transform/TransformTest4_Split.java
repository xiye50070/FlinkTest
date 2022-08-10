package com.wmz.apiTest.transform;

import com.wmz.apiTest.beans.SensorReading;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

public class TransformTest4_Split {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> input = env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt");
        DataStream<String> input2 = env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt");

        DataStream<SensorReading> ds = input.map(line -> {
            String[] splits = line.split(",");
            return new SensorReading(splits[0], new Long(splits[1]), new Double(splits[2]));
        });

        DataStream<SensorReading> ds2 = input2.map(line -> {
            String[] splits = line.split(",");
            return new SensorReading(splits[0], new Long(splits[1]), new Double(splits[2]));
        });

        ConnectedStreams<SensorReading, SensorReading> connect = ds.connect(ds2);
        connect.map(new CoMapFunction<SensorReading, SensorReading, SensorReading>() {
            @Override
            public SensorReading map1(SensorReading value) throws Exception {
                return new SensorReading();
            }

            @Override
            public SensorReading map2(SensorReading value) throws Exception {
                return null;
            }
        });


    }

}
