package com.muye.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muye.model.entity.Question;
import com.muye.service.QuestionService;
import com.muye.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author LeeSanJin
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2024-09-02 20:23:06
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




