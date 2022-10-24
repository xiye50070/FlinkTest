package com.wmz.apiTest.cep;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Map;

/**
 * @author: Edwards wang
 * @description: TODO
 * @date: 2022/10/8 10:36
 * @version: 1.0
 */
public class LoginFailDetect {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        KeyedStream<LoginEvent, String> stream = env.fromElements(
                        new LoginEvent("user_1", "192.168.0.1", "fail", 2000L),
                        new LoginEvent("user_1", "192.168.0.2", "fail", 3000L),
                        new LoginEvent("user_2", "192.168.1.29", "fail", 4000L),
                        new LoginEvent("user_1", "171.56.23.10", "fail", 5000L),
                        new LoginEvent("user_2", "192.168.1.29", "success", 6000L),
                        new LoginEvent("user_2", "192.168.1.29", "fail", 7000L),
                        new LoginEvent("user_2", "192.168.1.29", "fail", 8000L)
                )
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<LoginEvent>forMonotonousTimestamps()
                                .withTimestampAssigner(
                                        new SerializableTimestampAssigner<LoginEvent>() {
                                            @Override
                                            public long extractTimestamp(LoginEvent element, long recordTimestamp) {
                                                return element.timestamp;
                                            }
                                        }
                                )
                ).keyBy(r -> r.userId);

        Pattern<LoginEvent, LoginEvent> pattern = Pattern.<LoginEvent>begin("first")
                .where(new SimpleCondition<LoginEvent>() {
                    @Override
                    public boolean filter(LoginEvent value) throws Exception {
                        return value.eventType.equals("fail");
                    }
                })
                .times(1,2).greedy();
//                .next("second")
//                .where(new SimpleCondition<LoginEvent>() {
//                    @Override
//                    public boolean filter(LoginEvent value) throws Exception {
//                        return value.eventType.equals("fail");
//                    }
//                })
//                .next("third")
//                .where(new SimpleCondition<LoginEvent>() {
//                    @Override
//                    public boolean filter(LoginEvent value) throws Exception {
//                        return value.eventType.equals("fail");
//                    }
//                });


        PatternStream<LoginEvent> patternStream = CEP.pattern(stream, pattern);

        SingleOutputStreamOperator<String> resultDS = patternStream.select(new PatternSelectFunction<LoginEvent, String>() {
            @Override
            public String select(Map<String, List<LoginEvent>> map) throws Exception {
                return map.get("first").toString();


//                LoginEvent first = map.get("first").get(0);
//                LoginEvent second = map.get("first").get(1);
//                LoginEvent third = map.get("first").get(2);
//                LoginEvent fourth = map.get("first").get(3);
////                LoginEvent second = map.get("second").get(0);
////                LoginEvent third = map.get("third").get(0);
//                return first.userId + "连续三次登陆失败!, 登陆时间:  " +
//                        first.timestamp + ", " +
//                        second.timestamp + ", " +
//                        third.timestamp + ","+
//                        fourth.timestamp + "!";
            }
        });

        resultDS.print("Warning");

        env.execute();
    }
}
