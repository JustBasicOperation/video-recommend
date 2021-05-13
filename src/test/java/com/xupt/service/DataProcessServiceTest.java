package com.xupt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xupt.entity.PreferenceEntity;
import com.xupt.entity.User;
import com.xupt.entity.Video;
import com.xupt.mapper.PreferenceMapper;
import com.xupt.mapper.UserMapper;
import com.xupt.mapper.VideoMapper;
import com.xupt.service.impl.PreferenceServiceImpl;
import com.xupt.vo.ClickReportVO;
import com.xupt.vo.PreferenceVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataProcessServiceTest {

    @Resource
    DataProcessService service;

    @Resource
    UserMapper userMapper;

    @Resource
    VideoMapper videoMapper;

    @Resource
    PreferenceServiceImpl serviceImpl;

    @Resource
    PreferenceMapper preferenceMapper;

    @Resource
    RecommendService recommendService;

    /**
     * 视频数据入库初始化
     */
    @Test
    public void initVideoData() {
        service.initVideoData();
    }

    /**
     * 用户主动评价数据初始化
     */
    @Test
    public void initRecData() {
        //数据库数据初始化
        List<User> userList = userMapper.selectList(
                new QueryWrapper<>());
        List<Video> videoList = videoMapper.selectList(new QueryWrapper<>());
        for (User user : userList) {
            LinkedList<PreferenceEntity> list = new LinkedList<>();
            for (Video video : videoList) {
                PreferenceEntity entity = new PreferenceEntity();
                entity.userId = user.userId;
                entity.itemId = video.getVideoId();
                //产生0-10的随机数
                entity.score = (int)(Math.random()*10);
                entity.created = new Date();
                list.add(entity);
            }
            serviceImpl.saveBatch(list);
            list.clear();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 模拟用户偏好数据上报流程
     */
    @Test
    public void reportPreference() {
        List<PreferenceEntity> list = preferenceMapper.selectList(new QueryWrapper<PreferenceEntity>().last("limit 0,1000"));
        for (PreferenceEntity entity : list) {
            PreferenceVO preferenceVO = new PreferenceVO();
            preferenceVO.userId = entity.userId;
            preferenceVO.itemId = entity.itemId;
//            recommendService.appendCsv(preferenceVO,entity.score);
        }
    }

    /**
     * 模拟用户的点击行为，触发计算推荐列表的流程
     */
    @Test
    public void reportClickData() {
        //模拟第一个用户的点击行为
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda().eq(User::getUserId,"u8079671854606372864"));
        List<Video> videos = videoMapper.selectList(new QueryWrapper<Video>()
                .lambda().orderByDesc(Video::getVideoId).last("limit 60,90"));
        LinkedList<ClickReportVO> reportVOS = new LinkedList<>();
        //模拟每10条上报一次
        for (int i = 0; i < videos.size(); i++) {
            ClickReportVO reportVO = new ClickReportVO();
            reportVO.itemId = videos.get(i).getVideoId();
            reportVO.userId = user.userId;
            reportVOS.add(reportVO);
            if(i % 10 == 0) {
                recommendService.reportClick(reportVOS);
                reportVOS.clear();
            }
        }
        try {
            //沉睡3秒，等待上报完成
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试用户偏好数据上报性能
     */
    @Test
    public void testReportPreference() {
        List<PreferenceEntity> list = preferenceMapper.selectList(
                new QueryWrapper<PreferenceEntity>().last("limit 0,100000"));
//        long start = System.currentTimeMillis();
//        recommendService.appendCsv(list);
//        System.out.println("测试用户偏好数据上报性能（1000条）:" + (System.currentTimeMillis() - start) + "ms");

        long start = System.currentTimeMillis();
        for (PreferenceEntity entity : list) {
            LinkedList<PreferenceEntity> list1 = new LinkedList<>();
            list1.add(entity);
            recommendService.appendCsv(list1);
        }
        System.out.println("测试用户偏好数据上报性能（100000条）:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 测试获取推荐列表接口
     */
    @Test
    public void testGetRecommendList() {
        List<User> users = userMapper.selectList(new QueryWrapper<>());
//        recommendService.recommend();
        long start = System.currentTimeMillis();
        int i = 0;
        for (User user : users) {
            if (i == 15) {
                break;
            }
            i++;
            recommendService.getRecommendList(user.userId);
        }
        System.out.println("测试获取推荐列表接口:" + (System.currentTimeMillis() - start) + "ms");
    }
}