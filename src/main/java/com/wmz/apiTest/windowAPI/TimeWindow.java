package com.wmz.apiTest.windowAPI;

import com.wmz.apiTest.beans.SensorReading;
import org.apache.commons.collections.IteratorUtils;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class TimeWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

//        DataStream<String> ds = env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt");
        DataStreamSource<String> inputStream = env.socketTextStream("192.168.165.34", 7777);

        SingleOutputStreamOperator<SensorReading> mapStream = inputStream.map((MapFunction<String, SensorReading>) s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0],new Long(fields[1]),new Double(fields[2]));
        });



        DataStream<Integer> resultStream = mapStream.keyBy("id")
//                .countWindow(10,10)
//                .window(EventTimeSessionWindows.withGap(Time.seconds(15)))
                .window(TumblingProcessingTimeWindows.of(Time.seconds(15)))
                .aggregate(new AggregateFunction<SensorReading, Integer, Integer>() {

                    @Override
                    public Integer createAccumulator() {
                        return 0;
                    }

                    @Override
                    public Integer add(SensorReading value, Integer accumulator) {
                        return ++accumulator;
                    }

                    @Override
                    public Integer getResult(Integer accumulator) {
                        return accumulator;
                    }

                    @Override
                    public Integer merge(Integer a, Integer b) {
                        return a + b;
                    }
                });
//                .reduce((ReduceFunction<SensorReading>) (value1, value2) -> null);



        SingleOutputStreamOperator<Integer> resultStream2 = mapStream.keyBy("id")
                .window(TumblingProcessingTimeWindows.of(Time.seconds(15)))
//                .trigger()
//                .process(new ProcessWindowFunction<SensorReading, Object, Tuple, org.apache.flink.streaming.api.windowing.windows.TimeWindow>() {
//                    @Override
//                    public void process(Tuple tuple, ProcessWindowFunction<SensorReading, Object, Tuple, org.apache.flink.streaming.api.windowing.windows.TimeWindow>.Context context, Iterable<SensorReading> elements, Collector<Object> out) throws Exception {
//                        context.
//                    }
//                })
                .apply(new WindowFunction<SensorReading, Integer, Tuple, org.apache.flink.streaming.api.windowing.windows.TimeWindow>() {
                    @Override
                    public void apply(Tuple tuple, org.apache.flink.streaming.api.windowing.windows.TimeWindow window, Iterable<SensorReading> input, Collector<Integer> out) throws Exception {
                        int count = IteratorUtils.toList(input.iterator()).size();
                        out.collect(count);
                    }
                });



        mapStream.keyBy("id")
                        .countWindow(10,2)
                                .aggregate(new MyAvgTemp());


        resultStream.print();

        env.execute();
    }
    public static class MyAvgTemp implements  AggregateFunction<SensorReading, Tuple2<Double,Integer>,Double>{

        @Override
        public Tuple2<Double, Integer> createAccumulator() {
            return new Tuple2<>(0.0,0);
        }

        @Override
        public Tuple2<Double, Integer> add(SensorReading value, Tuple2<Double, Integer> accumulator) {
            return new Tuple2<>(accumulator.f0 + value.getTemperature(),++accumulator.f1);
        }

        @Override
        public Double getResult(Tuple2<Double, Integer> accumulator) {
            return accumulator.f0/accumulator.f1;
        }

        @Override
        public Tuple2<Double, Integer> merge(Tuple2<Double, Integer> a, Tuple2<Double, Integer> b) {
            return new Tuple2<>(a.f0+b.f0,a.f1 + b.f1);
        }
    }
}
