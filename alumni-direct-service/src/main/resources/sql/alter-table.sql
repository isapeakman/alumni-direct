-- 为面试会话表添加评估报告字段
ALTER TABLE interview_session
    ADD COLUMN evaluation_report TEXT         NULL COMMENT 'AI面试评估报告',
    ADD COLUMN candidate_name    VARCHAR(100) NULL COMMENT '候选人姓名',
    ADD COLUMN desired_position  VARCHAR(100) NULL COMMENT '目标岗位';
