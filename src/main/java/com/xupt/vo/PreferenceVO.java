package com.xupt.vo;

import lombok.Data;

@Data
public class PreferenceVO {
    public String userID;
    public String itemID;
    public Integer status;//0:喜欢,1:不喜欢,2:默认
}
