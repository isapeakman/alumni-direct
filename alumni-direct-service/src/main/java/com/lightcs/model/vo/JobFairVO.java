package com.lightcs.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-18
 * @Description: 活动视图
 * @Version: 1.0
 */
@Data
public class JobFairVO {
    private Integer id;
    private Integer type; // 活动类型    0 招聘会 1 宣讲会
    private String organizer; // 举办方
    private String company; // 招聘公司
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;   // 活动开始时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;  // 活动结束时间
    private String location;
    private String description;
    private String name; // 活动名称
    private String imageUrl; // 图片地址
}
