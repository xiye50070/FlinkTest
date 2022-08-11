package com.wmz.apiTest.stateApi;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class PeriodicPvExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<Event> stream = env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.getTimestamp();
                            }
                        }));

        stream.print("input");

       // 统计每个用户的Pv
        stream.keyBy(Event::getUser)
                        .process(new PeriodicPvResult())
                                .print();

        env.execute();
    }

    // 实现自定义的keyedProcessFunction
    public static class PeriodicPvResult extends KeyedProcessFunction<String,Event,String>{

        // 定义状态保存当前pv统计值
        ValueState<Long> countState;

        // 判断是否有定时器
        ValueState<Long> timerTsState;

        @Override
        public void open(Configuration parameters) throws Exception {
            countState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("count",Long.class));
            timerTsState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timerTs",Long.class));
        }

        @Override
        public void processElement(Event value, KeyedProcessFunction<String, Event, String>.Context ctx, Collector<String> out) throws Exception {
            // 每来一条数据，就更新对应的count值
            Long count = countState.value();
            countState.update(count == null ? 1 : ++count);

            // 注册定时器。如果没有注册过的话才去注册定时器
            if (timerTsState.value() == null){
                ctx.timerService().registerEventTimeTimer(value.getTimestamp() + 10*1000L);
                timerTsState.update(value.getTimestamp() + 10*1000L);
            }
        }

        @Override
        public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
            // 定时器触发，输出一次统计结果
            out.collect(ctx.getCurrentKey() + " pv: " + countState.value());
            timerTsState.clear();
        }
    }
}
