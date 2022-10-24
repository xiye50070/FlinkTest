package com.wmz.apiTest.tableAndSqlAPI;

import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class TopNExample {
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

        // 普通TopN，选取当前所有用户中，浏览量最大的2个
        Table topNResultTable = tableEnv.sqlQuery("SELECT user_name, cnt, row_num " +
                " FROM ( " +
                " SELECT *, ROW_NUMBER() OVER ( " +
                "     ORDER BY cnt DESC " +
                "   ) AS row_num  " +
                " FROM ( SELECT user_name, COUNT(url) AS cnt FROM clickTable GROUP BY user_name ) " +
                ") WHERE row_num <=2 "
        );

//        tableEnv.toChangelogStream(topNResultTable).print("topN");



        String subQueury = " SELECT user_name, COUNT(url) AS cnt, window_start, window_end" +
                " FROM TABLE ( " +
                    " TUMBLE(TABLE clickTable, DESCRIPTOR(et), INTERVAL '10' SECOND )" +
                ") " +
                " GROUP BY user_name,window_start,window_end";


        // 窗口TopN，统计一段时间内的活跃用户Top2
        Table windowTopNResultTable = tableEnv.sqlQuery("SELECT user_name, cnt, row_num, window_end" +
                " FROM ( " +
                " SELECT *, ROW_NUMBER() OVER ( " +
                "       PARTITION BY window_start, window_end " +
                "       ORDER BY cnt DESC " +
                "   ) AS row_num  " +
                " FROM ( " + subQueury + " ) " +
                ") WHERE row_num <=2 "
        );

        DataStreamSink<Row> topN = tableEnv.toChangelogStream(windowTopNResultTable).print("topN");


        env.execute();

    }
}
