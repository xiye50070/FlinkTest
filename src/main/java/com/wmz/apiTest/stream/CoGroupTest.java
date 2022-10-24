package com.wmz.apiTest.stream;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

import java.time.Duration;

import static org.apache.flink.table.api.Expressions.$;

public class CoGroupTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        env.setParallelism(1);

        SingleOutputStreamOperator<Tuple2<String, Long>> stream1 = env.fromElements(
                Tuple2.of("a", 1000L),
//                Tuple2.of("b", 1000L),
//                Tuple2.of("a", 2000L),
                Tuple2.of("b", 5100L)
        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple2<String, Long>>forBoundedOutOfOrderness(Duration.ZERO)
                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple2<String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple2<String, Long> element, long recordTimestamp) {
                        return element.f1;
                    }
                }));


        SingleOutputStreamOperator<Tuple2<String, Integer>> stream2 = env.fromElements(
                Tuple2.of("a", 3000),
                Tuple2.of("b", 4000),
//                Tuple2.of("a", 4500),
                Tuple2.of("b", 5500)
        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple2<String, Integer>>forBoundedOutOfOrderness(Duration.ZERO)
                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple2<String, Integer>>() {
                    @Override
                    public long extractTimestamp(Tuple2<String, Integer> element, long recordTimestamp) {
                        return element.f1;
                    }
                }));


        Table table1 = tableEnv.fromDataStream(stream1);
//        Table table2 = tableEnv.fromDataStream(stream2)
//                .select($("f1").as("time"),$("f0").as("id"))
////                .renameColumns()
//                .groupBy($("id")).select($("time").max());



        Table table3 = tableEnv.fromDataStream(stream2);
        table3.execute().print();
        String sql = "select count(*),f0 as aa from " + table3 + " group by f0";
//        tableEnv.sqlQuery(sql).execute().print();
        String s = tableEnv.explainSql(sql);
        System.out.println(s);

//        String explain = tableEnv.explain(table2);
//        System.out.println(explain);


//        Table table = table1.leftOuterJoin(table2, $("f0").isEqual($("id")));
//        tableEnv.toChangelogStream(table).print();

//        table1.printSchema();

//        stream1.join(stream2)
//                .where(data -> data.f0)
//                .equalTo(data -> data.f0)

//        stream1.coGroup(stream2)
//                .where(data -> data.f0)
//                .equalTo(data -> data.f0)
//                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
//                .apply(new CoGroupFunction<Tuple2<String, Long>, Tuple2<String, Integer>, String>() {
//                    @Override
//                    public void coGroup(Iterable<Tuple2<String, Long>> first, Iterable<Tuple2<String, Integer>> second, Collector<String> out) throws Exception {
//                        out.collect(first + "=>" + second);
//                    }
//                }).print();


        env.execute();
    }
}
