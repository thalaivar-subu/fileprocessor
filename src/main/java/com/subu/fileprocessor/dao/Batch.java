package com.subu.fileprocessor.dao;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@Data
@RequiredArgsConstructor
public class Batch {
    private final Integer number;
    private final ArrayList<String> list;
    private final Long previousBatchCharOffset;
}
