//package develop.lowcodeModel;
//
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//
//
//public class DevelopMainTest {
//    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        DataStream<String> ds = env.readTextFile("X:\\IdeaProjects\\FlinkTest\\src\\main\\resources\\sourceTest2.txt");
//        public String develop_map(DataStream<String> ds) {
//            return ds.map((MapFunction<String, Integer>) s -> s.length());
//        }
//        env.execute();
//    }
//}
