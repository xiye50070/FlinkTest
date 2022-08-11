package com.wmz.apiTest.stateApi;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.time.Duration;

public class FakeWindowExample {
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

        stream.keyBy(Event::getUrl)
                        .process(new FakeWindowResult(10L * 1000))
                                .print();


        env.execute();
    }

    public static class FakeWindowResult extends KeyedProcessFunction<String,Event,String>{

        // 窗口大小
        private Long windowSize;

        public FakeWindowResult(Long windowSize) {
            this.windowSize = windowSize;
        }

        // 定义一个mapState，用来保存每个窗口中统计的count值
        MapState<Long,Long> windowUrlCountMapState;

        @Override
        public void open(Configuration parameters) throws Exception {
            windowUrlCountMapState = getRuntimeContext().getMapState(new MapStateDescriptor<>("window-count",Long.class,Long.class));
        }

        @Override
        public void processElement(Event value, KeyedProcessFunction<String, Event, String>.Context ctx, Collector<String> out) throws Exception {
            // 每来一条数据，应该根据时间戳判断属于哪个窗口
            Long windowStart = value.getTimestamp() / windowSize * windowSize;
            Long windowEnd = windowStart + windowSize;

            // window能包含的最大时间戳，作为窗口触发定时器，注册end-1的定时器
            ctx.timerService().registerEventTimeTimer(windowEnd-1);

            // 更新状态进行增量聚合
            if (windowUrlCountMapState.contains(windowStart)){
                Long count = windowUrlCountMapState.get(windowStart);
                windowUrlCountMapState.put(windowStart,count + 1);
            }else {
                windowUrlCountMapState.put(windowStart,1L);
            }
        }

        // 定时器触发时触发计算

        @Override
        public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
            Long windowEnd = timestamp+1;
            Long windowStart = windowEnd - windowSize;
            Long count = windowUrlCountMapState.get(windowStart);

            out.collect("窗口：" + new Timestamp(windowStart) + "~" + new Timestamp(windowEnd)
                    + " url: " + ctx.getCurrentKey()
                    + " count: " + count);

            // 模拟窗口关闭，清除map中的对应kv
            windowUrlCountMapState.remove(windowStart);
        }
    }


}
