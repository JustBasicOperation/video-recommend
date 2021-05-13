package com.xupt.controller;

import com.xupt.dto.RecordDTO;
import com.xupt.dto.ResponseDTO;
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
    public ResponseDTO<String> registerUser(UserVO vo){
        String id = userService.registerUser(vo);
        ResponseDTO<String> responseDTO = ResponseDTO.of();
        return responseDTO.success(id);
    }

    /**
     * 用户登录
     * @param userName userName
     * @param password password
     * @return return
     */
    @PostMapping("/login")
    public ResponseDTO<String> login(@RequestParam("userName") String userName,
                             @RequestParam("password") String password){
        String login = userService.login(userName, password);
        ResponseDTO<String> res = ResponseDTO.of();
        return res.success(login);
    }

    /**
     * 获取喜欢列表
     * @param vo vo
     * @return return
     */
    @GetMapping("/record")
    public ResponseDTO<List<RecordDTO>> getRecords(RecordVO vo){
        List<RecordDTO> recordDTOS = userService.getRecords(vo);
        ResponseDTO<List<RecordDTO>> res = ResponseDTO.of();
        return res.success(recordDTOS);
    }
}
