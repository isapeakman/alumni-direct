package com.lightcs.controller;

import com.lightcs.model.pojo.JobApprovalRecord;
import com.lightcs.service.JobApprovalRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// todo 职位审批记录
@Tag(name = "职位审批记录")
@RestController
@RequestMapping("/jobApprovalRecord")
public class JobApprovalRecordController {

    @Autowired
    private JobApprovalRecordService jobApprovalRecordService;

    @GetMapping("/{id}")
    public JobApprovalRecord getJobApprovalRecordById(@PathVariable Integer id) {
        return jobApprovalRecordService.getById(id);
    }

    @GetMapping("/all")
    public List<JobApprovalRecord> getAllJobApprovalRecords() {
        return jobApprovalRecordService.list();
    }

    @PutMapping
    public boolean updateJobApprovalRecord(@RequestBody JobApprovalRecord jobApprovalRecord) {
        return jobApprovalRecordService.updateById(jobApprovalRecord);
    }

    @DeleteMapping("/{id}")
    public boolean deleteJobApprovalRecord(@PathVariable Integer id) {
        return jobApprovalRecordService.removeById(id);
    }
}