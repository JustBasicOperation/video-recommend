package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video")
public class Video {
    @TableId("video_id")
    private String videoId;
    private String url;
    private String coverAddress;
    private int type;
    private String title;
    private Date created;
}
