package com.wmz.apiTest.tableAndSqlAPI;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

import static org.apache.flink.table.api.Expressions.$;


public class TimeAndWindowTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //1、在创建表的DDL中直接定义时间属性
        String createDDL = "CREATE TABLE clickTable (" +
                " user_name STRING, " +
                " url STRING, " +
                " ts  BIGINT, " +
                " et AS TO_TIMESTAMP( FROM_UNIXTIME(ts/1000) ), " +
                " WATERMARK FOR et AS et - INTERVAL '1' SECOND " +
                ") with (" +
                " 'connector' = 'filesystem'," +
                " 'path' = 'input/click.txt'," +
                " 'format' = 'csv' " +
                ")";

        tableEnv.executeSql(createDDL);


        //2、在流转换成Table的时候，定义时间属性
        SingleOutputStreamOperator<Event> stream = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        }));


        Table table = tableEnv.fromDataStream(stream,
                Schema.newBuilder()
                        .columnByExpression("user_name", $("user"))
                        .columnByExpression("url_1", $("url"))
                        .columnByExpression("ts", $("timestamp").as("ts"))
//                        .columnByExpression("et", $("et").rowtime())
                        .columnByExpression("rowtime","CAST(TO_TIMESTAMP(FROM_UNIXTIME(`timestamp`)) AS TIMESTAMP(3))")
                        .build()
        );

//        Table table = tableEnv.fromDataStream(stream, $("user"), $("url"), $("timestamp").as("ts"),
//                $("et").rowtime());

        // 聚合查询转换
        //1、分组聚合
        Table aggTable = tableEnv.sqlQuery("select user_name, count(1) from clickTable group by user_name");

//        tableEnv.toChangelogStream(aggTable).print("aggTable");

        // 2、 窗口聚合
        // 2.1 滚动窗口
        Table tumbWindowResultTable = tableEnv.sqlQuery("select user_name, COUNT(1) as cnt, " +
                "   window_end as endT " +
                " from TABLE( " +
                "   TUMBLE(TABLE clickTable, DESCRIPTOR(et), INTERVAL '10' SECOND) " +
                ") " +
                "GROUP BY user_name, window_end, window_start"
        );

//        tableEnv.toChangelogStream(tumbWindowResultTable).print("tumb");

        // 2.2 滑动窗口
        Table hopWindowResultTable = tableEnv.sqlQuery("select user_name, COUNT(1) as cnt, " +
                "   window_end as endT " +
                " from TABLE( " +
                "   HOP(TABLE clickTable, DESCRIPTOR(et), INTERVAL '5' SECOND, INTERVAL '10' SECOND) " +
                ") " +
                "GROUP BY user_name, window_end, window_start"
        );

//        tableEnv.toChangelogStream(hopWindowResultTable).print("hop");

        // 2.3 累积窗口
        Table cumulateWindowResultTable = tableEnv.sqlQuery("select user_name, COUNT(1) as cnt, " +
                "   window_end as endT " +
                " from TABLE( " +
                "   CUMULATE(TABLE clickTable, DESCRIPTOR(et), INTERVAL '5' SECOND, INTERVAL '10' SECOND) " +
                ") " +
                "GROUP BY user_name, window_end, window_start"
        );

//        tableEnv.toChangelogStream(cumulateWindowResultTable).print("cumulate");

        // 2.4 开窗聚合Over
        Table overWindowResultTable = tableEnv.sqlQuery("select user_name, " +
                " avg(ts) OVER ( " +
                "   PARTITION BY user_name " +
                "   ORDER BY et " +
                "   ROWS BETWEEN 3 PRECEDING AND CURRENT ROW " +
                " ) AS avg_ts " +
                " FROM clickTable "
        );
        tableEnv.toChangelogStream(overWindowResultTable).print("over");
        env.execute();


    }
}
