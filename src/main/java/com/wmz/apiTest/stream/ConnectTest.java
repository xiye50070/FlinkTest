package com.wmz.apiTest.stream;

import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.scala.typeutils.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.types.Row;

public class ConnectTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Integer> stream1 = env.fromElements(1, 2, 3);
        DataStreamSource<Long> stream2 = env.fromElements(1L, 2L, 3L);



        DataStreamSource<Row> rowDataStreamSource = env.fromElements(Row.of("1"),
                Row.of("2"));

        SingleOutputStreamOperator<Row> rowStream = rowDataStreamSource.returns(Row.class);

        rowStream.print();


        DataStream<Long> returns = stream2.returns(Long.class);

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
