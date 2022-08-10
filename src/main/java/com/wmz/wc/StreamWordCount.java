package com.wmz.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.lang.reflect.Parameter;

public class StreamWordCount {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(6);
//        String input = "X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\hello.txt";
//        DataStreamSource<String> inputSource = env.readTextFile(input);
        ParameterTool pt = ParameterTool.fromArgs(args);
        String host = pt.get("host");
        int port = pt.getInt("port");
        DataStreamSource<String> socketSource = env.socketTextStream(host,port);

        socketSource.flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                String[] strs = s.split(" ");
                for (String str: strs){
                    collector.collect(new Tuple2<>(str,1));
                }
            }
        }).keyBy(0).sum(1).slotSharingGroup("1").print();



        env.execute();
    }
}
