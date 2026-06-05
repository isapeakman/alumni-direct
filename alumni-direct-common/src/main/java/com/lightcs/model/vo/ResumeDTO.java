package com.lightcs.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 简历结构化数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 所在城市
     */
    private String location;

    /**
     * 学历
     */
    private String education;

    /**
     * 专业
     */
    private String major;

    /**
     * 工作年限
     */
    private String experienceYears;

    /**
     * 期望职位
     */
    private String desiredPosition;

    /**
     * 期望薪资
     */
    private String desiredSalary;

    /**
     * 教育经历
     */
    private List<EducationExperience> educationExperience;

    /**
     * 工作经历
     */
    private List<WorkExperience> workExperience;

    /**
     * 项目经验
     */
    private List<ProjectExperience> projectExperience;

    /**
     * 技能列表
     */
    private List<String> skills;

    /**
     * 证书列表
     */
    private List<String> certificates;

    /**
     * 自我评价
     */
    private String selfEvaluation;

    /**
     * 原始文本（用于调试）
     */
    private String rawText;

    /**
     * 教育经历
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationExperience {
        private String school;
        private String degree;
        private String major;
        private String startDate;
        private String endDate;
        private String description;
    }

    /**
     * 工作经历
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkExperience {
        private String company;
        private String position;
        private String startDate;
        private String endDate;
        private String description;
    }

    /**
     * 项目经验
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectExperience {
        private String projectName;
        private String role;
        private String startDate;
        private String endDate;
        private String description;
        private String technologies;
    }
}
