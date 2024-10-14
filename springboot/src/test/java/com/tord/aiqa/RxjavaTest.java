package com.tord.aiqa;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RxjavaTest {
    @Test
    public void Test01() throws InterruptedException {
        //创建数据流
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(i -> i + 1)
                .subscribeOn(Schedulers.io());

        //订阅数据流
        flowable.observeOn(Schedulers.io())
                .doOnNext(i -> System.out.println(i.toString()))
                .subscribe();

        Thread.sleep(11000L);
    }
}
