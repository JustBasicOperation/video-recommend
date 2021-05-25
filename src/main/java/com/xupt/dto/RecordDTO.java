package com.xupt.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RecordDTO {
    public long totalPage;
    public List<Record> records;
}
