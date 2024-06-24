package com.subu.fileprocessor.dao;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class Offset {
    private final long lineOffset;
    private final long charOffset;
}

