package com.xupt.controller;

import com.xupt.dto.RecordDTO;
import com.xupt.dto.ResponseDTO;
import com.xupt.service.UserService;
import com.xupt.vo.RecordVO;
import com.xupt.vo.UserVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin(origins = "*")
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
     * 浏览器用户登录
     * @param userName userName
     * @param password password
     * @return return
     */
    @GetMapping("/loginClient")
    public ResponseDTO<String> loginClient(@RequestParam("userName") String userName,
                                   @RequestParam("password") String password) {
        String login = userService.login(userName, password);
        ResponseDTO<String> dto = ResponseDTO.of();
        return dto.success(login);
    }

    /**
     * 获取用户点赞列表
     * @param vo vo
     * @return return
     */
    @GetMapping("/praise")
    public RecordDTO getRecords(RecordVO vo) {
        return userService.getRecords(vo);
    }

    /**
     * 获取用户播放记录
     * @param vo vo
     * @return return
     */
    @GetMapping("/history")
    public RecordDTO getHistoryRecords(RecordVO vo) {
        RecordDTO recordDTO = userService.getHistoryRecords(vo);
        return recordDTO;
    }
}
