package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("video_user")
public class User {
    @TableId(type = IdType.AUTO)
    public Long id;
    public String userID;
    public String userName;
    public String userPassword;
}
