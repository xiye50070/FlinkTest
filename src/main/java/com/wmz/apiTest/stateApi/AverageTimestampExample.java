package com.wmz.apiTest.stateApi;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class AverageTimestampExample {
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


        stream.keyBy(Event::getUser)
                        .flatMap(new AvgTsResult(5L))
                                .print();
        env.execute();
    }

    // 自定义实现平均时间戳的统计
    public static class AvgTsResult extends RichFlatMapFunction<Event,String>{
        // 定义一个聚合的状态，用来保存平均时间戳
        AggregatingState<Event,Long> avgTsAggState;

        // 定义一个值状态，保存用户访问的次数
        ValueState<Long> countState;

        private Long count;

        public AvgTsResult(Long count) {
            this.count = count;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            avgTsAggState = getRuntimeContext().getAggregatingState(new AggregatingStateDescriptor<Event, Tuple2<Long,Long>, Long>(
                    "avg-ts",
                    new AggregateFunction<Event, Tuple2<Long, Long>, Long>() {
                        @Override
                        public Tuple2<Long, Long> createAccumulator() {
                            return Tuple2.of(0L,0L);
                        }

                        @Override
                        public Tuple2<Long, Long> add(Event value, Tuple2<Long, Long> accumulator) {
                            return Tuple2.of(accumulator.f0 + value.getTimestamp(),accumulator.f1 + 1);
                        }

                        @Override
                        public Long getResult(Tuple2<Long, Long> accumulator) {
                            return accumulator.f0 / accumulator.f1;
                        }

                        @Override
                        public Tuple2<Long, Long> merge(Tuple2<Long, Long> a, Tuple2<Long, Long> b) {
                            return null;
                        }
                    },
                    Types.TUPLE(Types.LONG,Types.LONG)
            ));

            countState = getRuntimeContext().getState(new ValueStateDescriptor<>("count",Long.class));
        }

        @Override
        public void flatMap(Event value, Collector<String> out) throws Exception {
            // 每来一条数据，currcount + 1
            Long currConut = countState.value();
            if (currConut == null){
                currConut = 1L;
            }else {
                currConut = currConut + 1;
            }

            // 更新状态
            countState.update(currConut);
            avgTsAggState.add(value);

            // 如果达到count次数就输出结果
            if (currConut.equals(count)){
                out.collect(value.getUser() + "过去" + count + "次访问平均时间戳为" + avgTsAggState.get());

                countState.clear();
                avgTsAggState.clear();
            }
        }
    }
}
