package com.wmz.apiTest.tableAndSqlAPI;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

public class CommonApiTest {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 创建表
        String createDDL = "CREATE TABLE clickTable (" +
                " user_name STRING, " +
                " url STRING, " +
                " ts  BIGINT " +
                ") with (" +
                " 'connector' = 'filesystem'," +
                " 'path' = 'input/click.txt'," +
                " 'format' = 'csv' " +
                ")";

        tableEnv.executeSql(createDDL);


        // 创建一张用于输出的表
        String createOutDDL = "CREATE TABLE outTable (" +
                " user_name STRING, " +
                " url STRING " +
                ") with (" +
                " 'connector' = 'filesystem'," +
                " 'path' = 'output'," +
                " 'format' = 'csv' " +
                ")";

        tableEnv.executeSql(createOutDDL);


        String createPrintOutDDL = "CREATE TABLE printOutTable (" +
                " user_name STRING, " +
                " url STRING " +
                ") with (" +
                " 'connector' = 'print'" +
                ")";
        tableEnv.executeSql(createPrintOutDDL);
        // 调用Table API进行表的查询转换
        Table clickTable = tableEnv.from("clickTable");
        Table resultTable = clickTable.where($("user_name").isEqual("Bob"))
                .select($("user_name"), $("url"));

        tableEnv.createTemporaryView("result2",resultTable);

        // 执行SQL进行表的查询转换
        Table resultTable2 = tableEnv.sqlQuery("select user_name,url from result2");

        // 输出表
        resultTable2.executeInsert("printOutTable");


    }
}
