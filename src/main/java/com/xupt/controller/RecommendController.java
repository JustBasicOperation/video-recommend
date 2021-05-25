package com.xupt.controller;

import com.xupt.entity.Video;
import com.xupt.vo.ClickReportVO;
import com.xupt.service.RecommendService;
import com.xupt.vo.PreferenceVO;
import com.xupt.vo.VideoVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin(origins = "*")
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
    public List<VideoVO> getRecommendList(@RequestParam(value = "userID") String userID) {
        return recommendService.getRecommendList(userID);
    }

    /**
     * 获取单条视频
     * @param videoID 视频id
     * @return return
     */
    @GetMapping("/single")
    public Video getVideo(@RequestParam(value = "videoId") String videoID) {
        return recommendService.getVideo(videoID);
    }

    /**
     * 用户浏览记录上报(由点击行为触发,上报kafka)
     * 数据入库保存
     * kafka消费者生成推荐列表，结果保存至redis
     * @return return
     */
    @PostMapping("/history")
    public Boolean reportClick(@RequestBody List<ClickReportVO> list) {
        recommendService.reportClick(list);
        return true;
    }

    /**
     * 用户喜好数据上报
     * 1.数据入库
     * 2.追加到csv文件
     * 3.计算相似度，结果更新至redis
     * @return return
     */
    @PostMapping("/prefer")
    public Boolean reportPreference(@RequestBody List<PreferenceVO> vos) {
        recommendService.reportPreference(vos);
        return true;
    }

    /**
     * 获取热点榜单
     */
    @GetMapping("/hotspot")
    public List<VideoVO> getHotspotList() {
        return recommendService.getHotspot();
    }
}
