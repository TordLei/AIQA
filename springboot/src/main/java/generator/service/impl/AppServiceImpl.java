package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tord.aiqa.mapper.AppMapper;
import com.tord.aiqa.model.entity.App;
import generator.service.AppService;
import org.springframework.stereotype.Service;

/**
* @author tordlei
* @description 针对表【app(应用)】的数据库操作Service实现
* @createDate 2024-09-23 20:39:39
*/
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>
    implements AppService{

}




