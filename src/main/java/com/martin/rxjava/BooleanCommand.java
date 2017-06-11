package com.martin.rxjava;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class BooleanCommand extends HystrixCommand<Boolean> implements IIndexAware<Boolean>
{
    private int index;

    public BooleanCommand(int index)
    {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Boolean")).andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(7000))
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(1)));
        this.index = index;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    protected Boolean run() throws Exception
    {
        System.out.println(index + ": Boolean command running");
        Thread.sleep(5000);
        return true;
    }
}
