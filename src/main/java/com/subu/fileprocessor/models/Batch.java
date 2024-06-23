package com.subu.fileprocessor.models;

import lombok.Getter;

import java.util.List;

@Getter
public class Batch {
    private Integer number;
    private List<String> list;

    public Batch(int number, List<String> list) {
        this.number = number;
        this.list = list;
    }
}
