package com.wmz.apiTest.tableAndSqlAPI;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

import static org.apache.flink.table.api.Expressions.$;

public class SimpleTableExample {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 读取数据，得到DataStream
        SingleOutputStreamOperator<Event> eventStream = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.getTimestamp();
                            }
                        }));

        // 创建一个表执行环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 将DataStream转换成table
        Table eventTable = tableEnv.fromDataStream(eventStream);


        // 直接写SQL进行转换
        Table resultTable = tableEnv.sqlQuery("select user , url ,`timestamp` from " + eventTable);

        // 基于TableAPI直接转换
        Table resultTable2 = eventTable.select($("user"), $("url"))
                .where($("user").isEqual("Alice"));




//        tableEnv.toDataStream(resultTable).print("result");
//        tableEnv.toDataStream(resultTable2).print("result2");


        // 聚合转换
        tableEnv.createTemporaryView("clickTable",eventTable);
        tableEnv.executeSql("select * from clickTable").print();
//        Table aggResult = tableEnv.sqlQuery("select user, count(url) as cnt from clickTable group by user");
//        tableEnv.toChangelogStream(aggResult).print("agg");



        env.execute();
    }
}

/**
 * CREATE CATALOG myhive WITH (
 *     'type' = 'hive',
 *     'default-database' = 'mydatabase',
 *     'hive-conf-dir' = '/opt/hive/conf'
 * );
 */

