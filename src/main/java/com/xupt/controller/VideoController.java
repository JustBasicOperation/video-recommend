package com.xupt.controller;

import com.xupt.vo.ClickReportVO;
import com.xupt.dto.ResponseDTO;
import com.xupt.entity.Article;
import com.xupt.service.RecommendService;
import com.xupt.vo.PreferenceVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("/video")
public class VideoController {

    @Resource
    RecommendService recommendService;

    /**
     * 获取推荐列表
     * @param userID 用户id
     * @return return
     */
    @GetMapping("/list")
    public ResponseDTO<List<Article>> getRecommendList(@RequestParam(value = "userID") String userID){
        List<Article> recommendList = recommendService.getRecommendList(userID);
        ResponseDTO<List<Article>> dto = ResponseDTO.of();
        return dto.success(recommendList);
    }

    /**
     * 用户浏览记录上报(由点击行为触发)
     * @return return
     */
    @PostMapping("/history")
    public ResponseDTO reportHistory(@RequestBody ClickReportVO vo){
        recommendService.reportHistory(vo);
        return ResponseDTO.of().success("report success!");
    }

    /**
     * 用户喜好数据上报
     * @return return
     */
    @PostMapping("/prefer")
    public ResponseDTO reportPreference(@RequestBody PreferenceVO vo){
        recommendService.reportPreference(vo);
        return ResponseDTO.of().success("report success!");
    }

    @PostMapping("/user")
    public ResponseDTO<String> registerUser(){
        String userId = recommendService.registerUser();
        ResponseDTO<String> dto = ResponseDTO.of();
        return dto.success(userId);
    }
}
