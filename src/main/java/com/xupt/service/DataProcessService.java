package com.xupt.service;

import com.csvreader.CsvReader;
import com.xupt.entity.Video;
import com.xupt.mapper.VideoMapper;
import com.xupt.util.SnowFlake;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 数据预处理Service
 */
@Service
public class DataProcessService {

    @Resource
    VideoMapper videoMapper;

    //数据预处理，将从B站爬取到的视频数据入库
    public void initVideoData(){
        String path = "C:\\Users\\Administrator\\Desktop\\论文\\2.csv";
        try {
            CsvReader reader = new CsvReader(path, ',', StandardCharsets.UTF_8);
            reader.readHeaders();
            while(reader.readRecord()) {
                String url = reader.get("视频链接");
                String coverAddress = reader.get("视频封面链接");
                String title = reader.get("标题");
                Video video = new Video();
                video.setUrl(url);
                video.setCover_address(coverAddress);
                video.setTitle(title);
                video.setType(1);
                video.setVideoId(String.valueOf(SnowFlake.nextId()));
                video.setCreated(new Date());
                videoMapper.insert(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
