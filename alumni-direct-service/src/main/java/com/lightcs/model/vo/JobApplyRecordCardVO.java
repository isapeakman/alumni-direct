package com.lightcs.model.vo;

import lombok.Data;

import java.util.Date;

/**
 *  求职申请卡片
 */
@Data
public class JobApplyRecordCardVO {
    /**
     *  id
     */
    private Integer id;
//    /**
//     * 职位id
//     */
//    private Integer jobId;
    /**
     * 简历附件
     */
    private String resume;
//    /**
//     * 申请状态 0 已申请 1 已接收 2 已拒绝
//     */
//    private Integer status;
    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 申请人id
     */
    private Integer applicantId;
    /**
     * 申请人姓名
     */
    private String applicantName;
    /**
     * 学校
     */
    private String school;
    /**
     * 专业
     */
    private String major;
    /**
     * 学历
     */
    private String degree;
    /**
     *  年龄
     */
    private Integer age;
    /**
     * 性别
     */
    private Integer gender;

}
