package com.wmz.apiTest.processApi;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.util.Iterator;


public class KeyedProcessFunctionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> stream = env.addSource(new ClickSource());


        stream.keyBy(Event::getUser).process(new KeyedProcessFunction<String, Event, String>() {

            ListState<String> listState;

            @Override
            public void open(Configuration parameters) throws Exception {
                listState = getRuntimeContext().getListState(new ListStateDescriptor<String>("",String.class));
            }

            @Override
            public void processElement(Event value, KeyedProcessFunction<String, Event, String>.Context ctx, Collector<String> out) throws Exception {
                long currTs = ctx.timerService().currentProcessingTime();
                out.collect(ctx.getCurrentKey() + "数据到达，到达时间： " + new Timestamp(currTs));
                listState.add(ctx.getCurrentKey());
                ctx.timerService().registerProcessingTimeTimer(currTs + 10 * 1000L);
            }

            @Override
            public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                Iterable<String> iterable = listState.get();
                Iterator<String> iterator = iterable.iterator();
                int i = 0;

                while (iterator.hasNext()){
                    String next = iterator.next();
                    System.out.println("#_____" + next);
                    i++;
                }
                out.collect("定时器触发，触发时间：" + new Timestamp(timestamp) + "#" + i);
            }
        }).print();

        env.execute();
    }
}
