package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 招聘会/宣讲会
 *
 * @TableName job_fair
 */
@TableName(value = "job_fair")
@Data
public class JobFair implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 类型: 0招聘会，1宣讲会
     */
    private Integer type;

    /**
     * 主办方
     */
    private String organizer;

    /**
     * 招聘公司
     */
    private String company;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 举办地点
     */
    private String location;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系方式
     */
    private String contactWay;

    /**
     * 图片
     */
    private String imageUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private Integer createId;

    /**
     * 招聘者
     */
    private Integer recruiterId;
    /**
     * 活动描述
     */
    private String description;
    /**
     * 招聘会名称
     */
    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}