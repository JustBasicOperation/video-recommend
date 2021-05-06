package com.xupt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xupt.entity.Record;
import com.xupt.entity.User;
import com.xupt.mapper.RecordMapper;
import com.xupt.mapper.UserMapper;
import com.xupt.util.SnowFlake;
import com.xupt.vo.RecordVO;
import com.xupt.vo.UserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    RecordMapper recordMapper;

    public String registerUser(UserVO vo) {
        User user = new User();
        long id = SnowFlake.nextId();
        user.userId = String.valueOf(id);
        user.userPassword = vo.userPassword;
        user.userName = vo.userName;
        user.phoneNumber = vo.phoneNumber;
        //TODO insertOrUpdate
        int insert = userMapper.insert(user);
        return String.valueOf(id);
    }

    public String login(String userName, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda()
                .eq(User::getUserName, userName)
                .eq(User::getUserPassword, password));
        return user == null ? "用户名不存在或者密码错误" : user.userId;
    }

    public List<Record> getRecords(RecordVO vo) {
        IPage<Record> page = new Page<>();
        page.setSize(vo.getPageSize());
        page.setCurrent(vo.pageNum);
        IPage<Record> recordIPage = recordMapper.selectPage(page,
                new QueryWrapper<Record>().lambda().eq(Record::getUserId, vo.userId));
        return recordIPage.getRecords();
    }
}
