package com.wmz.apiTest.transform;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class TransformTest1_Base {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> ds = env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt");

        SingleOutputStreamOperator<Integer> mapStream = ds.map(new MapFunction<String, Integer>() {
            @Override
            public Integer map(String s) throws Exception {
                return s.length();
            }
        });

//        ds.flatMap(new FlatMapFunction<String, String>() {
//            @Override
//            public void flatMap(String s, Collector<String> collector) throws Exception {
//                String[] split = s.split(",");
//                for (String str : split){
//                    collector.collect(str);
//                }
//
//            }
//        }).print();


//        map.print();
//        ds.print("s2");
        env.execute();

    }
    public DataStream<Integer> mapStream(DataStream<String> ds){
        return ds.map((MapFunction<String, Integer>) s -> s.length());
    }


    public static class MyMapper extends RichMapFunction<String, String>{

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);

        }

        @Override
        public String map(String value) throws Exception {

            return null;
        }
    }
}
