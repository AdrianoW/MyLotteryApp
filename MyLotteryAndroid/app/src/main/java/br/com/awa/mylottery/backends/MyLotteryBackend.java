package br.com.awa.mylottery.backends;

/**
 * Created by adriano on 12/01/16.
 */
public class MyLotteryBackend {
    private static MyLotteryBackend ourInstance = new MyLotteryBackend();

    public static MyLotteryBackend getInstance() {
        return ourInstance;
    }

    private MyLotteryBackend() {
    }
}
