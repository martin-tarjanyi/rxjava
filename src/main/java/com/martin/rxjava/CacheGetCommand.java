package com.martin.rxjava;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class CacheGetCommand extends HystrixCommand<String> implements IIndexAware
{
    private int index;

    public CacheGetCommand(int i)
    {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("cache_get")).andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(250)));
        index = i;
    }

    @Override
    protected String run() throws Exception
    {
        System.out.println(index + ": Calling cache, Thread name:" + Thread.currentThread().getName());
        Thread.sleep(200);

        if (Math.random() < 0.7)
        {
            throw new RuntimeException("Cache connection failed");
        }

        return index + ": from_cache";
//                return null;
    }

    @Override
    public int getIndex()
    {
        return index;
    }
}
