package com.xupt.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClickReportVO implements Serializable {
    public String userID;
    public String itemID;
}
