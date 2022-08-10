package develop.lowcodeModel;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

public class FlinkTransformModel {

    public DataStream<Integer> develop_mapFunction(DataStream<String> ds){
        DataStream<Integer> mapStream = ds.map(new MapFunction<String, Integer>() {
            @Override
            public Integer map(String s) throws Exception {
                return s.length();
            }
        });

        return mapStream;
    }
}
