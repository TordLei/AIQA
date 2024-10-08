package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tord.aiqa.mapper.UserAnswerMapper;
import com.tord.aiqa.model.entity.UserAnswer;
import generator.service.UserAnswerService;
import org.springframework.stereotype.Service;

/**
* @author tordlei
* @description 针对表【user_answer(用户答题记录)】的数据库操作Service实现
* @createDate 2024-09-23 20:39:39
*/
@Service
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer>
    implements UserAnswerService{

}




