package com.lightcs.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-26
 * @Description:
 * @Version: 1.0
 */

@Data
public class JobApplyRecordVO {
    private Integer jobId;
    private String title;
    private Integer jobType;
    private String location;
    private String jobDesc;
    private Integer minSalary;
    private Integer maxSalary;
    private String companyName;
    private String recruiterName;
    private String recruiterAvatar;
    private Integer status; //对用户来说  2 已发布 其他状态则显示 职位已关闭
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applyTime;
    private String resume;
}
