package completableFutureTest;

import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompletableFutureTest {
    public static void main(String[] args) throws Exception{
        Units.printTimeAndThread("小白进入餐厅");
        Units.printTimeAndThread("小白点了  番茄炒蛋 + 米饭");

        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                Units.printTimeAndThread("厨师炒菜");
                Units.threadSleep(200L);
                return "番茄炒蛋";
            }
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            Units.printTimeAndThread("服务员蒸饭");
            Units.threadSleep(300L);
            return "米饭";
        }), new BiFunction<String, String, String>() {
            @Override
            public String apply(String s, String s2) {
                return String.format("%s + %s 好了", s, s2);
            }
        });

        Units.printTimeAndThread("小白在打极地大乱斗");
        Units.printTimeAndThread(String.format("%s 小白开吃",cf1.join()));
    }


}
