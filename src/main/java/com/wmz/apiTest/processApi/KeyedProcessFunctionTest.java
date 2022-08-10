package com.wmz.apiTest.processApi;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;


public class KeyedProcessFunctionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> stream = env.addSource(new ClickSource());


        stream.keyBy(Event::getUser).process(new KeyedProcessFunction<String, Event, String>() {

            @Override
            public void processElement(Event value, KeyedProcessFunction<String, Event, String>.Context ctx, Collector<String> out) throws Exception {
                long currTs = ctx.timerService().currentProcessingTime();
                out.collect(ctx.getCurrentKey() + "数据到达，到达时间： " + new Timestamp(currTs));

                ctx.timerService().registerProcessingTimeTimer(currTs + 10 * 1000L);
            }

            @Override
            public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                out.collect("定时器触发，触发时间：" + new Timestamp(timestamp));
            }
        }).print();

        env.execute();
    }
}
