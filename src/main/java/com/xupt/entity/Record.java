package com.xupt.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Record {
    public String userId;
    public String videoId;
    public Date created;
}
