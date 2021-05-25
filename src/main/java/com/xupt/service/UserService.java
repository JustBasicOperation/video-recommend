package com.xupt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xupt.constant.Constant;
import com.xupt.dto.Record;
import com.xupt.dto.RecordDTO;
import com.xupt.entity.HistoryEntity;
import com.xupt.entity.PreferenceEntity;
import com.xupt.entity.User;
import com.xupt.entity.Video;
import com.xupt.mapper.HistoryMapper;
import com.xupt.mapper.PreferenceMapper;
import com.xupt.mapper.UserMapper;
import com.xupt.mapper.VideoMapper;
import com.xupt.service.impl.PreferenceService;
import com.xupt.util.SnowFlake;
import com.xupt.vo.RecordVO;
import com.xupt.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    VideoMapper  videoMapper;

    @Resource
    PreferenceService preferenceService;

    @Resource
    HistoryMapper historyMapper;

    public String registerUser(UserVO vo) {
        User user = new User();
        long id = SnowFlake.nextId();
        user.userId = "u" + id;
        user.userPassword = vo.userPassword;
        user.userName = vo.userName;
        //insertOrUpdate
        User res = userMapper.selectOne(
                new QueryWrapper<User>().lambda().eq(User::getUserName, vo.getUserName()));
        if(res != null) {
            return "用户名已存在！";
        } else {
            int insert = userMapper.insert(user);
            return "注册成功！";
        }
    }

    public String login(String userName, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda()
                .eq(User::getUserName, userName)
                .eq(User::getUserPassword, password));
        return user == null ? "用户名不存在或者密码错误" : user.userId;
    }

    public RecordDTO getRecords(RecordVO vo) {
        IPage<PreferenceEntity> page = new Page<>();
        page.setSize(vo.getPageSize());
        page.setCurrent(vo.getPageNum());
        IPage<PreferenceEntity> records = preferenceService.page(page,
                new QueryWrapper<PreferenceEntity>().lambda()
                        .eq(PreferenceEntity::getUserId, vo.getUserId())
                        .orderByDesc(PreferenceEntity::getCreated));
        LinkedList<Record> list = new LinkedList<>();
        for (PreferenceEntity record : records.getRecords()) {
            Video video = videoMapper.selectOne(
                    new QueryWrapper<Video>().lambda().eq(Video::getVideoId, record.getItemId()));
            Record dto = new Record();
            dto.setUserId(record.getUserId());
            dto.setVideoId(record.getUserId());
            dto.setTitle(video.getTitle());
            dto.setCreated(Constant.date2String(record.getCreated()));
            list.add(dto);
        }
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setRecords(list);
        recordDTO.setTotalPage(records.getPages());
        return recordDTO;
    }

    public RecordDTO getHistoryRecords(RecordVO vo) {
        IPage<HistoryEntity> page = new Page<>();
        page.setSize(vo.getPageSize());
        page.setCurrent(vo.getPageNum());
        IPage<HistoryEntity> records = historyMapper.selectPage(page, new QueryWrapper<HistoryEntity>().lambda()
                .eq(HistoryEntity::getUserId, vo.getUserId())
                .orderByDesc(HistoryEntity::getCreated));
        LinkedList<Record> list = new LinkedList<>();
        for (HistoryEntity record : records.getRecords()) {
            Video video = videoMapper.selectOne(
                    new QueryWrapper<Video>().lambda().eq(Video::getVideoId, record.getItemId()));
            Record dto = new Record();
            dto.setUserId(record.getUserId());
            dto.setVideoId(record.getUserId());
            dto.setTitle(video.getTitle());
            dto.setCreated(Constant.date2String(record.getCreated()));
            list.add(dto);
        }
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setRecords(list);
        recordDTO.setTotalPage(records.getPages());
        return recordDTO;
    }
}
