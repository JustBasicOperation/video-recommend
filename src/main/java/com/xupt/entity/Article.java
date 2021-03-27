package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video_article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String url;
    private Date created;
}
