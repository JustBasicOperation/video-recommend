package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video_preference")
public class PreferenceEntity {
    @TableId(type = IdType.AUTO)
    public Long id;
    public String userID;
    public String itemID;
    public Integer score;//用户偏好程度，十分制
    public Date created;
}
