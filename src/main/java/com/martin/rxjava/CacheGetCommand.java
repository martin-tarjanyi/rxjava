package com.martin.rxjava;

import java.util.concurrent.TimeoutException;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class CacheGetCommand extends HystrixCommand<String>
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
                throw new TimeoutException();
//            return index + ": from_cache";
//                return null;
    }
}
