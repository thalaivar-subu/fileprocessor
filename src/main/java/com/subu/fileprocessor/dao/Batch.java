package com.subu.fileprocessor.dao;

import java.util.ArrayList;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Batch {

  private final Integer number;
  private final ArrayList<String> list;
  private final Long previousBatchCharOffset;
}
