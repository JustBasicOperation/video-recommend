package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("video_user")
public class User {
    public String userId;
    public String userName;
    public String userPassword;
}
