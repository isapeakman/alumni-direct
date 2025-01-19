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
 * @TableName job
 */
@TableName(value = "job")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Job implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 职位名称
     */
    private String title;

    /**
     * 职位类型: 0全职、1实习、2兼职
     */
    private Integer jobType;

    /**
     * 职位描述
     */
    private String jobDesc;

    /**
     * 工作地点
     */
    private String location;

    /**
     * 薪资范围
     */
    private String salaryRange;
    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 创建用户
     */
    private Integer createId;
    /**
     * 状态  0 待审批 1待发布 2 已发布 3 关闭
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 发布时间
     */
    private Date publishTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}