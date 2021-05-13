package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("video_user")
public class User {
    @TableId(value = "user_id")
    public String userId;
    public String userName;
    public String userPassword;
}
