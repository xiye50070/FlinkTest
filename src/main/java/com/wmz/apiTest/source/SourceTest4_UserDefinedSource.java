package com.wmz.apiTest.source;

import com.wmz.apiTest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import scala.Int;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SourceTest4_UserDefinedSource {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<SensorReading> ds = env.addSource(new MySensorSource());
        ds.print();
        env.execute();
    }


    public static class MySensorSource implements SourceFunction<SensorReading> {
        //定义一个标志位，用来控制数据的生产
        private boolean running = true;

        @Override
        public void run(SourceContext<SensorReading> sourceContext) throws Exception {
            Random random = new Random();

            Map<String, Double> sensorTempMap = new HashMap<>();

            for (int i = 0; i<10;i++){
                sensorTempMap.put("sensor_" + ++i, 60 + random.nextGaussian()*20);
            }

            while (running){
                for (String id : sensorTempMap.keySet()){
                    Double newTemp = sensorTempMap.get(id) + random.nextGaussian();
                    sensorTempMap.put(id,newTemp);
                    sourceContext.collect(new SensorReading(id,System.currentTimeMillis(),newTemp));
                }
                TimeUnit.SECONDS.sleep(1);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}
