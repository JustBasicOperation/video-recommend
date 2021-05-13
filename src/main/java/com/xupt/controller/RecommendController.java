package com.xupt.controller;

import com.xupt.entity.Video;
import com.xupt.vo.ClickReportVO;
import com.xupt.dto.ResponseDTO;
import com.xupt.service.RecommendService;
import com.xupt.vo.PreferenceVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController()
@RequestMapping("/video")
public class RecommendController {

    @Resource
    RecommendService recommendService;

    /**
     * 获取推荐列表
     * @param userID 用户id
     * @return return
     */
    @GetMapping("/list")
    public List<Video> getRecommendList(@RequestParam(value = "userID") String userID){
        return recommendService.getRecommendList(userID);
    }

    /**
     * 用户浏览记录上报(由点击行为触发,上报kafka )
     * @return return
     */
    @PostMapping("/history")
    public Boolean reportClick(@RequestBody List<ClickReportVO> list){
        recommendService.reportClick(list);
        return true;
    }

    /**
     * 用户喜好数据上报
     * @return return
     */
    @PostMapping("/prefer")
    public Boolean reportPreference(@RequestBody List<PreferenceVO> vos){
        recommendService.reportPreference(vos);
        return true;
    }

//    /**
//     * 注册用户
//     * @return userId
//     */
//    @PostMapping("/user")
//    public String registerUser(){
//        String userId = recommendService.registerUser();
//        ResponseDTO<String> dto = ResponseDTO.of();
//        return userId;
//    }

//    /**
//     * 推送内容入库
//     * @param vo 入参
//     * @return return
//     */
//    @PostMapping("/source")
//    public ResponseDTO<String> reportSource(@RequestBody SourceVO vo){
//        String id = recommendService.reportSource(vo);
//        ResponseDTO<String> dto = ResponseDTO.of();
//        return dto.success(id);
//    }
}
