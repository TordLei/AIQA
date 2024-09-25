package com.tord.aiqa;

import com.tord.aiqa.common.ErrorCode;
import com.tord.aiqa.exception.ThrowUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主类测试
 *

 */
@SpringBootTest
class MainApplicationTests {

    @Test
    void contextLoads() {
        ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR,"标题不能为空");
    }

}
