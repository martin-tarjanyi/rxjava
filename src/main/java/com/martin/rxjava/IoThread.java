package com.martin.rxjava;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class IoThread
{
    private static long START = 0L;

    public static void main(String[] args) throws InterruptedException
    {
        START = System.currentTimeMillis();

        Observable<String> first = Observable.fromCallable(IoThread::getString)
                                           .subscribeOn(Schedulers.io())
                                           .map(str -> str.substring(0, 3));

        Observable<String> second = Observable.fromCallable(IoThread::getString)
                                           .subscribeOn(Schedulers.io())
                                           .map(str -> str.substring(3, 5));

        Observable<String> third = Observable.fromCallable(IoThread::getString)
                                           .subscribeOn(Schedulers.io())
                                           .map(str -> str.substring(5, 7));

        Observable.zip(first, second, third, IoThread::concat).subscribe(IoThread::print);

        Thread.sleep(10000);
    }

    private static String getString()
    {
        System.out.println("GetString method thread: " + Thread.currentThread().getName());
        try
        {
            int millis = RandomUtils.nextInt(2000, 5000);
            System.out.println("This part is " + millis);
            Thread.sleep(millis);
            return "kiscica";
        } catch (InterruptedException e)
        {
            ExceptionUtils.rethrow(e);
            return "haha";
        }
    }

    private static String concat(String... strs)
    {
        return StringUtils.join(strs);
    }

    private static void print(String str)
    {
        System.out.println("Print method thread: " + Thread.currentThread().getName());

        System.out.println(str);

        System.out.println(System.currentTimeMillis() - START);
    }
}
