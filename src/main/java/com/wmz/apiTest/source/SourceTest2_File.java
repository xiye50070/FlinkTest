package com.wmz.apiTest.source;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import scala.collection.mutable.HashMap;

import java.util.Arrays;

public class SourceTest2_File {

    public static RowTypeInfo createSchema(){
        TypeInformation<?>[] typeInformations = new TypeInformation[3];
        String[] fieldNames = new String[3];

        typeInformations[0] = BasicTypeInfo.STRING_TYPE_INFO;
        typeInformations[1] = BasicTypeInfo.LONG_TYPE_INFO;
        typeInformations[2] = BasicTypeInfo.DOUBLE_TYPE_INFO;

        fieldNames[0] = "id";
        fieldNames[1] = "timeStamp";
        fieldNames[2] = "temprate";

        return new RowTypeInfo(typeInformations,fieldNames);

    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);




        DataStream<String> ds = env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt");
        DataStream<Row> rowStream = env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt").map(r -> {
            String[] strs = r.split(",");
            return Row.of(strs[0],Long.valueOf(strs[1]),Double.valueOf(strs[2]));
        }).returns(createSchema());
        rowStream.print("row");



        /**
         *     public static RowTypeInfo mockRowTypeInfo(List<MockSchema> mockDataSchema) {
         *         TypeInformation<?>[] types = new TypeInformation[mockDataSchema.size()];
         *         String[] fieldNames = new String[mockDataSchema.size()];
         *         for (int index = 0; index < mockDataSchema.size(); index++) {
         *             MockSchema schema = mockDataSchema.get(index);
         *             types[index] = schema.typeInformation();
         *             fieldNames[index] = schema.getName();
         *         }
         *         return new RowTypeInfo(types, fieldNames);
         *     }
         */
//        env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt").map(Row::of).returns(createSchema());
//        ds.map(new MapFunction<String, Row>() {
//            @Override
//            public Row map(String value) throws Exception {
//                String[] strs = value.split(",");
//
//                Tuple2<String, String> id = Tuple2.of("id", strs[0]);
//                Tuple2<String, String> timestamp = Tuple2.of("timestamp", strs[1]);
//                Tuple2<String, String> temprate = Tuple2.of("temprate", strs[2]);
//
//
//                Row vRow = new Row(strs.length);
//                vRow.
//                vRow.setField("id",strs[0]);
//                vRow.setField("time",strs[1]);
//                vRow.setField("tempr",strs[2]);
//                return vRow;
//            }
//        }).print("Row");
        ds.print("Str");
        env.execute();
    }


}
