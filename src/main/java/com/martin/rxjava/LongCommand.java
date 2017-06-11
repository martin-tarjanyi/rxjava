package com.martin.rxjava;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class LongCommand extends HystrixCommand<Long> implements IIndexAware<Long>
{
    private int index;

    public LongCommand(int index)
    {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Long")).andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(10000))
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(20)));
        this.index = index;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    protected Long run() throws Exception
    {
        System.out.println(index + ": Long command running");
        Thread.sleep(9000);
        throw new RuntimeException("byebye");
//        return (long) index;
    }
}
