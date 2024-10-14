package com.tord.aiqa.controller;

import com.tord.aiqa.common.BaseResponse;
import com.tord.aiqa.common.ResultUtils;
import com.tord.aiqa.mapper.UserAnswerMapper;
import com.tord.aiqa.model.dto.statistic.AppAnswerCountDTO;
import com.tord.aiqa.model.dto.statistic.AppAnswerResultCountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app/statistic")
public class AppStatisticController {
    @Resource
    private UserAnswerMapper userAnswerMapper;

    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppResultCount(Long appId) {
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }
}
