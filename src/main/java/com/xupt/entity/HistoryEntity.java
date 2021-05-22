package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video_history")
public class HistoryEntity {
    @TableId("user_id")
    public String userId;
    public String itemId;
    public Date created;
}
