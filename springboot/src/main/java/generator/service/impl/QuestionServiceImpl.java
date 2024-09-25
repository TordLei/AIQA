package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tord.aiqa.mapper.QuestionMapper;
import com.tord.aiqa.model.entity.Question;
import generator.service.QuestionService;
import org.springframework.stereotype.Service;

/**
* @author tordlei
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2024-09-23 20:39:39
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




