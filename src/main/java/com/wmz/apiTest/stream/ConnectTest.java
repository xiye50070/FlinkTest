package com.wmz.apiTest.stream;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

public class ConnectTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Integer> stream1 = env.fromElements(1, 2, 3);
        DataStreamSource<Long> stream2 = env.fromElements(1L, 2L, 3L);

        stream1.connect(stream2)
                .map(new CoMapFunction<Integer, Long, String>() {
                    @Override
                    public String map1(Integer value) throws Exception {
                        return "Integer: " + value.toString();
                    }

                    @Override
                    public String map2(Long value) throws Exception {
                        return "Long: " + value.toString();
                    }
                }).print();

        env.execute();

    }
}
