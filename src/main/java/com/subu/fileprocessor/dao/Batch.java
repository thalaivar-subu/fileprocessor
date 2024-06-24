package com.subu.fileprocessor.dao;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;

@Data
public class Batch {
    private final Integer number;
    private final ArrayList<String> list;
    private final Boolean poisonPill;

    public Batch(Integer number, ArrayList<String> list) {
        this.number = number;
        this.list = list;
        this.poisonPill = false;
    }

    public Batch(Integer number, ArrayList<String> list, Boolean poisonPill) {
        this.number = number;
        this.list = list;
        this.poisonPill = poisonPill;
    }
}
