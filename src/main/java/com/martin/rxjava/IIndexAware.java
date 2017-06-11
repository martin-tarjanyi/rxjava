package com.martin.rxjava;

import com.netflix.hystrix.HystrixInvokableInfo;

public interface IIndexAware<T> extends HystrixInvokableInfo<T>
{
    int getIndex();
}
