package com.xupt.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video_article")
public class Article {
    public String id;
    public String url;
    public String title;
    public Date created;
}
