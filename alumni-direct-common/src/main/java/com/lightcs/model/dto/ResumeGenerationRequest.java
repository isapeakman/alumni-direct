package com.lightcs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 简历生成请求DTO
 * 包含用户修改后的简历信息和求职岗位
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeGenerationRequest {

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
     * 期望职位（必填）
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
