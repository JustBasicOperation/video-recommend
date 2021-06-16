package com.xupt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xupt.entity.Video;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VideoMapper extends BaseMapper<Video> {

    @Select("SELECT * FROM video ORDER BY created DESC LIMIT #{start},#{dataSize};")
    List<Video> selectByLimit(@Param("start")int start,@Param("dataSize")int dataSize);
}
