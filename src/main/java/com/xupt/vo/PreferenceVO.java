package com.xupt.vo;

import lombok.Data;

@Data
public class PreferenceVO {
    public String userId;
    public String itemId;
    public Integer status;//0:喜欢,1:不喜欢,2:默认
}
