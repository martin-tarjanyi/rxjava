package com.martin.rxjava;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Dummy
{
    private final Long num;
    private final String string;
    private final Boolean bool;

    public Dummy(Long num, String string, Boolean bool)
    {
        this.num = num;
        this.string = string;
        this.bool = bool;
    }

    public Long getNum()
    {
        return num;
    }

    public String getString()
    {
        return string;
    }

    public Boolean getBool()
    {
        return bool;
    }
}
