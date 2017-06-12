package com.martin.rxjava;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class DownstreamCommand extends HystrixCommand<String> implements IIndexAware<String>
{
    private int index;

    public DownstreamCommand(int i)
    {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("downstream")).andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(10000))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(20)));

        index = i;
    }

    @Override
    protected String run() throws Exception
    {
        System.out.println(index + ": Calling downstream, Thread name: " + Thread.currentThread().getName());

        Thread.sleep(3000);

        if (Math.random() < 0.3)
        {
            throw new RuntimeException("Downstream command body threw an exception.");
        }

        return index + ": downstream";
    }

    @Override
    public int getIndex()
    {
        return index;
    }
}
