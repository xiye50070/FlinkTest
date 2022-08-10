package com.wmz.apiTest.processApi;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class ProcessFunctionTest {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<Event> stream = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.getTimestamp();
                            }
                        }));

        stream.process(new ProcessFunction<Event, Object>() {
            @Override
            public void processElement(Event value, ProcessFunction<Event, Object>.Context ctx, Collector<Object> out) throws Exception {
                if (value.getUser().equals("Mary")) {
                    out.collect(value.getUser() + "clicks" + value.getUrl());
                } else if (value.getUser().equals("Bob")) {
                    out.collect(value.getUser());
                    out.collect(value.getUser());
                }

                System.out.println("timestamp: " +  ctx.timestamp());
                System.out.println("watermark: " + ctx.timerService().currentWatermark());
                System.out.println(getRuntimeContext().getIndexOfThisSubtask());
            }
        }).print();


        env.execute();
    }
}
