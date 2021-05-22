package com.xupt.controller;

import com.xupt.dto.RecordDTO;
import com.xupt.service.UserService;
import com.xupt.vo.RecordVO;
import com.xupt.vo.UserVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController()
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    /**
     * 注册用户
     * @param vo 入参
     * @return return
     */
    @PostMapping("/register")
    public String registerUser(UserVO vo) {
        return userService.registerUser(vo);
    }

    /**
     * 用户登录
     * @param userName userName
     * @param password password
     * @return return
     */
    @GetMapping("/login")
    public String login(@RequestParam("userName") String userName,
                             @RequestParam("password") String password) {
        String login = userService.login(userName, password);
        return login;
    }

    /**
     * 获取用户点赞列表
     * @param vo vo
     * @return return
     */
    @GetMapping("/praise")
    public List<RecordDTO> getRecords(RecordVO vo) {
        return userService.getRecords(vo);
    }

    /**
     * 获取用户播放记录
     * @param vo vo
     * @return return
     */
    @GetMapping("/history")
    public List<RecordDTO> getHistoryRecords(RecordVO vo) {
        return userService.getHistoryRecords(vo);
    }
}
