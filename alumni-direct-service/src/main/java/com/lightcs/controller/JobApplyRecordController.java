package com.lightcs.controller;


import com.lightcs.service.JobApplyRecordService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobApplyRecord")
public class JobApplyRecordController {

    @Autowired
    private JobApplyRecordService jobApplyRecordService;

    @Operation(summary = "获取用户申请卡片")
    @GetMapping("/cards")
    public String getJobApplyRecordCards() {
        jobApplyRecordService.getJobApplyRecordCards();
    }

}
