package com.wmz.apiTest.stateApi;

import com.wmz.apiTest.source.ClickSource;
import com.wmz.apiTest.waterMark.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BufferingSinkExample {
    public static void main(String[] args) throws Exception{
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


        // 批量缓存输出
        stream.addSink(new BufferingSink(10));


        env.execute();
    }

    public static class BufferingSink implements SinkFunction<Event>, CheckpointedFunction {

        // 先定义当前类的属性，批量
        private final int threshold;

        public BufferingSink(int threshold) {
            this.threshold = threshold;
            this.bufferedElements = new ArrayList<>();
        }

        private List<Event> bufferedElements;

        // 定义一个算子状态
        private ListState<Event> checkpointedState;

        @Override
        public void invoke(Event value, Context context) throws Exception {
            bufferedElements.add(value);  // 缓存到列表

            // 判断如果达到阈值就批量写入
            if (bufferedElements.size() == threshold){
                // 用打印到控制台模拟写入外部系统
                for(Event element : bufferedElements){
                    System.out.println(element);
                }
                System.out.println("================================输出完毕==================================");
                bufferedElements.clear();
            }
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            // 清空已写入外部系统的数据
            checkpointedState.clear();


            // 对状态进行持久化，复制缓存的列表到列表状态
            for (Event element : bufferedElements){
                checkpointedState.add(element);
            }
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            // 定义算子状态
            ListStateDescriptor<Event> descriptor = new ListStateDescriptor<>("buffered-element", Event.class);

            checkpointedState = context.getOperatorStateStore().getListState(descriptor);

            // 如果从故障恢复需要将Liststate中的所有元素复制到列表中
            if(context.isRestored()){
                for (Event element : checkpointedState.get()){
                    bufferedElements.add(element);
                }
            }



        }
    }

}

