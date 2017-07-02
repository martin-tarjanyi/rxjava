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

        for (int i = 1; i <= 20; i++)
        {
            Thread.sleep(50);
            long startThread = System.currentTimeMillis();
            futures.add(getStringFuture(i));
            System.out.println("Creating thread in " + (System.currentTimeMillis() - startThread) + " milliseconds ");
        }

        for (Future<String> future : futures)
        {
            try
            {
                System.out.println("result: " + future.get());
            } catch (Exception e)
            {
            }
        }

        long end = System.currentTimeMillis();

        System.out.println(((end - start) / 1000d) + " seconds ");

        System.out.println("\nExecution times: " + HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().stream()
                .map(it -> it.getCommandGroup().toString() + ": " + it.getExecutionTimeInMilliseconds()).collect(Collectors.toList()));
    }

    private static Future<String> getStringFuture(int index)
    {
        return new CacheGetCommand(index).toObservable()
                                         .onErrorReturn(e -> null)  // we don't want the flow to abort, so we handle cache error as null
                                                                    // exception is not lost, logged by command hook
                                         .flatMap(cacheResult -> callDownstreamIfNeeded(cacheResult, index))
                                                                    // this it the point where we switch to downstream
                                                                    // if we have no result from cache
                                         .retry((count, exception) -> shouldRetry(index, count, exception))
                                                                    // only retries the downstream, not the cache
                                         .doOnError(e -> System.out.println("result: " + index + ": " + e.getMessage()))
                                                                    // this step is not needed
                                         .toBlocking().toFuture();
        // https://github.com/Netflix/Hystrix/blob/3eef024579a74c7d196906f43d2370585b00fc68/hystrix-core/src/main/java/com/netflix/hystrix/HystrixCommand.java#L378
    }

    private static boolean shouldRetry(int index, int count, Throwable exception)
    {
        if (count > 2)
        {
            System.out.println(index + ": downstream failed with '" + exception.getMessage() + "' no more retries");
            return false;
        }

        System.out.println(index + ": downstream failed with '" + exception.getMessage() + "' after " + count + " attempt(s)");
        return true;
    }

    private static Observable<String> callDownstreamIfNeeded(String cacheResult, int index)
    {
        System.out.println(index + ": name of the thread where we switch between cache and downstream: " + Thread.currentThread().getName());
        // Cache thread

        if (cacheResult != null)
        {
            // when cache is not null we can return with its result
            return Observable.just(cacheResult);
        }

        // else we switch to downstream
        return new DownstreamCommand(index).toObservable().doOnNext(result -> saveToCache(result, index));
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
