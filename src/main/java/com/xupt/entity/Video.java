package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video")
public class Video {
    private String videoId;
    private String url;
    private String cover_address;
    private int type;
    private String title;
    private Date created;
}
