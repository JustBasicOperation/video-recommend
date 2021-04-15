package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video_preference")
public class PreferenceEntity {
    @TableId(value = "user_id")
    public String userId;
    public String itemId;
    public Integer score;//用户偏好程度，十分制
    public Date created;
}
