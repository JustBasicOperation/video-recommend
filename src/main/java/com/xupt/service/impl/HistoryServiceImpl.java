package com.xupt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xupt.entity.HistoryEntity;
import com.xupt.mapper.HistoryMapper;
import org.springframework.stereotype.Service;

@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, HistoryEntity> implements HistoryService {
}
