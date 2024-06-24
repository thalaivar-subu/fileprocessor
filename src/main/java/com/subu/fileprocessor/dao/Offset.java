package com.subu.fileprocessor.dao;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class Offset {
    private final int lineOffset;
    private final int charOffset;
}

