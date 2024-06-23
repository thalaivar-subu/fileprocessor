package com.subu.fileprocessor.models;

import lombok.Getter;


@Getter
public class Offset {
    private final int lineOffset;
    private final int charOffset;

    public Offset(int lineOffset, int charOffset) {
        this.lineOffset = lineOffset;
        this.charOffset = charOffset;
    }
}

