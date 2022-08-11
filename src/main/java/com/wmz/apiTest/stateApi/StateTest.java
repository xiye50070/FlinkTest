package com.wmz.apiTest.stateApi;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class StateTest {
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

        stream.keyBy(Event::getUser)
                .flatMap(new MyFlatMap())
                .print();

        env.execute();
    }

    public static class MyFlatMap extends RichFlatMapFunction<Event,String>{

        ValueState<Event> myValueState;
        ListState<Event> myListState;
        MapState<String,Long> myMapState;
        ReducingState<Event> myReducingState;
        AggregatingState<Event,String> myAggregatingState;

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<Event> valueStateDescriptor = new ValueStateDescriptor<>("my-state", Event.class);
            myValueState = getRuntimeContext().getState(valueStateDescriptor);
            myListState = getRuntimeContext().getListState(new ListStateDescriptor<Event>("my-listState",Event.class));
            myMapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, Long>("my-MapState",String.class, Long.class));

            myReducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<>("my-ReducingState",
                    new ReduceFunction<Event>() {
                        @Override
                        public Event reduce(Event value1, Event value2) throws Exception {
                            return new Event(value1.getUser(), value1.getUrl(), value2.getTimestamp());
                        }
                    },
                    Event.class));

            myAggregatingState = getRuntimeContext().getAggregatingState(new AggregatingStateDescriptor<Event, Long, String>("my-agg", new AggregateFunction<Event, Long, String>() {
                @Override
                public Long createAccumulator() {
                    return 0L;
                }

                @Override
                public Long add(Event value, Long accumulator) {
                    return ++accumulator;
                }

                @Override
                public String getResult(Long accumulator) {
                    return "count: " + accumulator;
                }

                @Override
                public Long merge(Long a, Long b) {
                    return a + b;
                }
            }, Long.class));



            // 配置状态的TTL
            StateTtlConfig ttlConfig = StateTtlConfig.newBuilder(Time.hours(1))
                    .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                    // 状态过时处理策略
                    .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                    .build();

            valueStateDescriptor.enableTimeToLive(ttlConfig);

        }

        @Override
        public void flatMap(Event value, Collector<String> out) throws Exception {
            System.out.println(myValueState.value());
            myValueState.update(value);
//            System.out.println("my-value" + myValueState.value());

            myListState.add(value);

            myMapState.put(value.getUser(),myMapState.get(value.getUser()) == null ? 1 : myMapState.get(value.getUser()) + 1);
            System.out.println("my map value : " + value.getUser() + " " + myMapState.get(value.getUser()));

            myAggregatingState.add(value);
            System.out.println("my agg value: " + myAggregatingState.get());

            myReducingState.add(value);
            System.out.println("my reduce value: " + myReducingState.get());




        }
    }


}
