package com.martin.rxjava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import rx.Observable;

public class App
{
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException
    {
        HystrixRequestContext.initializeContext();
        HystrixPlugins.getInstance().registerCommandExecutionHook(new CommandHookLogger());

        long start = System.currentTimeMillis();

        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++)
        {
            Thread.sleep(100);
            long startThread = System.currentTimeMillis();
            futures.add(getStringFuture(i));
            System.out.println("Creating thread in " + (System.currentTimeMillis() - startThread) + " milliseconds ");
        }

        for (Future<String> future : futures)
        {
            System.out.println("result: " + future.get());
        }

        long end = System.currentTimeMillis();

        System.out.println(((end - start) / 1000d) + " seconds ");

        System.out.println("\nExecution times: " + HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().stream()
                .map(it -> it.getCommandGroup().toString() + ": " + it.getExecutionTimeInMilliseconds()).collect(Collectors.toList()));
    }

    private static Future<String> getStringFuture(int i)
    {
        return new CacheGetCommand(i).toObservable()
                                     .onErrorReturn(e -> null)  //exception is not lost, logged by command hook
                                     .flatMap(cacheResult -> callDownstreamIfNeeded(cacheResult, i))
                                     .retry(1L)
                                     .doOnNext(result -> saveToCache(result, i))
                                     .toBlocking().toFuture();
    }

    private static Observable<String> callDownstreamIfNeeded(String cacheResult, int i)
    {
        System.out.println("local method thread: " + Thread.currentThread().getName());

        if (cacheResult != null)
        {
            //when cache is not null we can return with its result
            return Observable.just(cacheResult);
        }

        return new DownstreamCommand(i).toObservable();
    }

    private static void saveToCache(String result, int index)
    {
        try
        {
            //we have to check if result came from cache before saving it
            //cache saving happens on downstream hystrix thread, but does not count to its timeout
            System.out.println(index + ": Saving response to cache: " + result + ", Thread name: " + Thread.currentThread().getName());
            Thread.sleep(200);
        } catch (Exception e)
        {
            System.out.println("error during save to cache");
        }
    }

}
