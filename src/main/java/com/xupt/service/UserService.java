package com.xupt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xupt.constant.Constant;
import com.xupt.dto.RecordDTO;
import com.xupt.entity.HistoryEntity;
import com.xupt.entity.PreferenceEntity;
import com.xupt.entity.User;
import com.xupt.mapper.HistoryMapper;
import com.xupt.mapper.PreferenceMapper;
import com.xupt.mapper.UserMapper;
import com.xupt.service.impl.PreferenceService;
import com.xupt.util.SnowFlake;
import com.xupt.vo.RecordVO;
import com.xupt.vo.UserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Resource
    UserMapper userMapper;

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

    public List<RecordDTO> getRecords(RecordVO vo) {
        IPage<PreferenceEntity> page = new Page<>();
        page.setSize(vo.getPageSize());
        page.setCurrent(vo.getPageNum());
        IPage<PreferenceEntity> records = preferenceService.page(page,
                new QueryWrapper<PreferenceEntity>().lambda()
                        .eq(PreferenceEntity::getUserId, vo.getUserId())
                        .orderByDesc(PreferenceEntity::getCreated));
        List<PreferenceEntity> list = records.getRecords();
        List<RecordDTO> collect = list.stream().map(ele -> {
            RecordDTO dto = new RecordDTO();
            dto.setUserId(ele.getUserId());
            dto.setVideoId(ele.getItemId());
            dto.setCreated(Constant.date2String(ele.getCreated()));
            return dto;
        }).collect(Collectors.toList());
        return collect;
    }

    public List<RecordDTO> getHistoryRecords(RecordVO vo) {
        IPage<HistoryEntity> page = new Page<>();
        page.setSize(vo.getPageSize());
        page.setCurrent(vo.getPageNum());
        IPage<HistoryEntity> records = historyMapper.selectPage(page, new QueryWrapper<HistoryEntity>().lambda()
                .eq(HistoryEntity::getUserId, vo.getUserId())
                .orderByDesc(HistoryEntity::getCreated));
        List<RecordDTO> collect = records.getRecords().stream().map(ele -> {
            RecordDTO dto = new RecordDTO();
            dto.setUserId(ele.getUserId());
            dto.setVideoId(ele.getUserId());
            dto.setCreated(Constant.date2String(ele.getCreated()));
            return dto;
        }).collect(Collectors.toList());
        return collect;
    }
}
