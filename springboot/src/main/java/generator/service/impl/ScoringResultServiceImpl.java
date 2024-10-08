package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tord.aiqa.mapper.ScoringResultMapper;
import com.tord.aiqa.model.entity.ScoringResult;
import generator.service.ScoringResultService;
import org.springframework.stereotype.Service;

/**
* @author tordlei
* @description 针对表【scoring_result(评分结果)】的数据库操作Service实现
* @createDate 2024-09-23 20:39:39
*/
@Service
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult>
    implements ScoringResultService{

}




