package com.lightcs.constants;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description: 职位常量
 * @Version: 1.0
 */

public interface JobConstant {
    // 职位状态
    Integer STATUS_PENDING = 0;// 待审核
    Integer STATUS_WAITING = 1;// 待发布
    Integer STATUS_OPENED = 2;//已发布
    Integer STATUS_CLOSED = 3;//已关闭
    Integer STATUS_FAIL = 4;//审核未通过
    // 审批状态
    Integer NOT_APPROVED = 0;//未审批
    Integer APPROVED = 1;//已审批
    // 审批结果
    Integer REJECTED = 0;//已驳回
    Integer PASS = 1;//通过
    Integer IS_ALUMNI = 1;//校友
    Integer NOT_ALUMNI = 0;//非校友
}
