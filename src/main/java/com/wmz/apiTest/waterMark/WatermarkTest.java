package com.wmz.apiTest.waterMark;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

public class WatermarkTest {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.getConfig().setAutoWatermarkInterval(100);

        env.fromElements(new Event("Bob","./cart",2000L),
                new Event("Alice","./prod?id=100",3000L),
                new Event ("Alice","./prod?id=2000",3500L))
                // 有序流的watermark生成
//                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps()
//                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
//                            @Override
//                            public long extractTimestamp(Event element, long recordTimestamp) {
//                                return element.timestamp;
//                            }
//                        }));


        // 乱序流的watermark生成
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                            @Override
                            public long extractTimestamp(Event element, long recordTimestamp) {
                                return element.timestamp;
                            }
                        }).withIdleness(Duration.ofSeconds(120)));



    }


    public static class CustomWatermarkStrategy implements WatermarkStrategy<Event> {

        // 处理乱序数据的逻辑
        @Override
        public WatermarkGenerator<Event> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
            return null;
        }

        // 相当于 .withTimestampAssigner()方法，定义时间戳列
        @Override
        public TimestampAssigner<Event> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
            return new SerializableTimestampAssigner<Event>() {
                @Override
                public long extractTimestamp(Event element, long recordTimestamp) {
                    return element.timestamp;
                }
            };
        }

        @Override
        public WatermarkStrategy<Event> withTimestampAssigner(TimestampAssignerSupplier<Event> timestampAssigner) {
            return WatermarkStrategy.super.withTimestampAssigner(timestampAssigner);
        }

        @Override
        public WatermarkStrategy<Event> withTimestampAssigner(SerializableTimestampAssigner<Event> timestampAssigner) {
            return WatermarkStrategy.super.withTimestampAssigner(timestampAssigner);
        }

        @Override
        public WatermarkStrategy<Event> withIdleness(Duration idleTimeout) {
            return WatermarkStrategy.super.withIdleness(idleTimeout);
        }
    }


    public static class CustomBoundedOutOfOrdernessGenerator implements WatermarkGenerator<Event>{
        // 延迟时间
        private Long delayTime = 5000L;


        //观察到的最大时间戳
        private Long maxTs = -Long.MAX_VALUE+delayTime + 1L;
        // 每次数据来时触发
        @Override
        public void onEvent(Event event, long eventTimestamp, WatermarkOutput output) {
            // 此处只更新时间戳
            maxTs = Math.max(event.timestamp, this.maxTs);
        }

        // 周期性发送
        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
            output.emitWatermark(new Watermark(maxTs - delayTime -1L));
        }
    }
}
