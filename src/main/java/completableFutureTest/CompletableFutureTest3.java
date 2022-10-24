package completableFutureTest;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureTest3 {
    public static void main(String[] args) {
        CompletableFuture<String> bus = CompletableFuture.supplyAsync(() -> {
            Units.printTimeAndThread("700路公交车正在赶来");
            Units.threadSleep(100L);
            return "700路到了";
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            Units.printTimeAndThread("800路公交正在赶来");
            Units.threadSleep(200L);
            return "800路到了";
        }), firstComeBus -> {
            Units.printTimeAndThread(firstComeBus);
            if (firstComeBus.startsWith("700")) {
                throw new RuntimeException("没油了。。。");
            }
            return firstComeBus;
        }).exceptionally(e -> {
            Units.printTimeAndThread(e.getMessage());
            Units.printTimeAndThread("小白叫出租车");
            return "出租车叫到了";
        });

        Units.printTimeAndThread(String.format("%s, 小白坐车回家", bus.join()));
    }
}
