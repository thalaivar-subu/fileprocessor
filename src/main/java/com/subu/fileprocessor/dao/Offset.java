package com.subu.fileprocessor.dao;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;


@Data
@RequiredArgsConstructor
public class Offset {
    private final BigInteger lineOffset;
    private final BigInteger charOffset;
}

