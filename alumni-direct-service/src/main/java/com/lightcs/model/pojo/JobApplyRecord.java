package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 职位申请表
 * @TableName job_apply_record
 */
@TableName(value = "job_apply_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplyRecord implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 职位id
     */
    private Integer jobId;

    /**
     * 申请人id
     */
    private Integer applicantId;

    /**
     * 简历附件
     */
    private String resume;
    /**
     * 描述信息
     */
    private String note;
    /**
     * 申请状态 0 已申请 1 已接收 2 已拒绝
     */
    private Integer status;

    /**
     * 申请时间
     */
    private Date applyTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}