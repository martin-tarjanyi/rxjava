package com.martin.rxjava;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import rx.Observable;

public class Zip
{
    public static void main(String[] args) throws InterruptedException
    {
        HystrixRequestContext.initializeContext();
        HystrixPlugins.getInstance().registerCommandExecutionHook(new CommandHookLogger());

        long start = System.currentTimeMillis();

        Observable.zip( // not blocking main thread
                new BooleanCommand(1).toObservable(),
                new DownstreamCommand(2).toObservable(),
                new LongCommand(3).toObservable(),
                // if one of the observables fail before the others are finished,
                // then the other unfinished observables will be unsubscribed
                // meaning command hook will be not called
                // thread seems to be released btw
                (bool, string, num) -> new Dummy(num, string, bool))
                  .subscribe(Zip::onNext, e -> {}, Zip::onCompleted);

        long finish = System.currentTimeMillis();

        System.out.println("Observables created in " + (finish - start) + " milliseconds");

        Thread.sleep(10000);
    }

    private static int calcHashCode(Dummy dummy)
    {
        // runs on the longest running thread, in this case downstream thread
        System.out.println("Name of the thread where map runs: " + Thread.currentThread().getName() + ", result: " + dummy.hashCode());
        return dummy.hashCode();
    }

    private static void onNext(Dummy dummy)
    {
        // runs on the longest running thread, in this case downstream thread
        System.out.println("Name of the thread where onNext runs: " + Thread.currentThread().getName() + ", result: " + dummy);
    }

    private static void onCompleted()
    {
        // runs on the longest running thread, in this case downstream thread
        System.out.println("Name of the thread where onCompleted runs: " + Thread.currentThread().getName());
    }
}
