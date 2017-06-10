package com.martin.rxjava;

import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;

public class CommandHookLogger extends HystrixCommandExecutionHook
{
    @Override
    public <T> void onSuccess(HystrixInvokable<T> commandInstance)
    {
        IIndexAware hystrixInvokableInfo = (IIndexAware) commandInstance;

        System.out.println(
                hystrixInvokableInfo.getIndex() + ": Command (" + hystrixInvokableInfo.getCommandGroup() + ") logging: SUCCESS");

        super.onSuccess(commandInstance);
    }

    @Override
    public <T> Exception onError(HystrixInvokable<T> commandInstance, HystrixRuntimeException.FailureType failureType, Exception e)
    {
        IIndexAware hystrixInvokableInfo = (IIndexAware) commandInstance;

        System.out.println(
                hystrixInvokableInfo.getIndex() + ": Command (" + hystrixInvokableInfo.getCommandGroup()
                        + "), logging: FAILURE: " + failureType + ", message: " + e.getMessage());

        return super.onError(commandInstance, failureType, e);
    }
}
