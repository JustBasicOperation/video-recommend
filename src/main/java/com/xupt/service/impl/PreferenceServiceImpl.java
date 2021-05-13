package com.xupt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xupt.entity.PreferenceEntity;
import com.xupt.mapper.PreferenceMapper;
import org.springframework.stereotype.Service;

@Service
public class PreferenceServiceImpl extends ServiceImpl<PreferenceMapper, PreferenceEntity> implements PreferenceService {
}
