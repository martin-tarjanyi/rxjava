package com.martin.rxjava;

import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;

public class CommandHookLogger extends HystrixCommandExecutionHook
{
    @Override
    public <T> void onSuccess(HystrixInvokable<T> commandInstance)
    {
        HystrixInvokableInfo<T> hystrixInvokableInfo = (HystrixInvokableInfo<T>) commandInstance;
        System.out.println("Command (" + hystrixInvokableInfo.getCommandGroup() + ") logging: SUCCESS");
        super.onSuccess(commandInstance);
    }

    @Override
    public <T> Exception onError(HystrixInvokable<T> commandInstance, HystrixRuntimeException.FailureType failureType, Exception e)
    {
        HystrixInvokableInfo<T> hystrixInvokableInfo = (HystrixInvokableInfo<T>) commandInstance;
        System.out.println("Command (" + hystrixInvokableInfo.getCommandGroup() + ") logging: FAILURE: " + failureType + "\n");
        return super.onError(commandInstance, failureType, e);
    }
}
