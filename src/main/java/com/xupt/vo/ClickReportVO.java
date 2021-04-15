package com.xupt.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClickReportVO implements Serializable {
    public String userId;
    public String itemId;
}
