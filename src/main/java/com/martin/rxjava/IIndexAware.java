package com.martin.rxjava;

import com.netflix.hystrix.HystrixInvokableInfo;

public interface IIndexAware extends HystrixInvokableInfo<String>
{
    int getIndex();
}
