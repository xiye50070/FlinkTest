package completableFutureTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureTest2 {
    public static void main(String[] args) {
        Units.printTimeAndThread("小白吃好了");
        Units.printTimeAndThread("小白 结账，要发票");

        CompletableFuture<String> invoice = CompletableFuture.supplyAsync(() -> {
            Units.printTimeAndThread("服务员1 收款500元");
            Units.threadSleep(1000L);
            return 500;
        }).thenApplyAsync(money -> {
            Units.printTimeAndThread("服务员2 开发票，金额：" + money);
            Units.threadSleep(200L);
            return money + " 元发票";
        });

        Units.printTimeAndThread("小白接受双排邀请");
        Units.printTimeAndThread("小白拿到 " + invoice.join() + ", 准备回家");
    }
}
