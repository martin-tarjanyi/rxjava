package com.martin.rxjava;

import java.util.concurrent.atomic.AtomicInteger;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class DownstreamCommand extends HystrixCommand<String>
{
    public static final AtomicInteger COMMAND_COUNTER = new AtomicInteger(0);
    private int index;

    public DownstreamCommand(int i)
    {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("downstream")).andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(3000))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(20)));

        index = i;
    }

    @Override
    protected String run() throws Exception
    {
        System.out.println(index + ": Calling downstream, Thread name: " + Thread.currentThread().getName());
        Thread.sleep(1500);
//        if (COMMAND_COUNTER.getAndIncrement() % 2 == 0)
//        {
//            throw new RuntimeException("Shit happened");
//        } else
//        {
        return index + ": downstream";
//        }
    }
}
