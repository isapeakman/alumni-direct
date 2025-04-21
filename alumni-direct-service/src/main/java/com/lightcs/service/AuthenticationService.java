package com.lightcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightcs.model.pojo.UserCertificationApproval;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-21
 * @Description:
 * @Version: 1.0
 */

public interface AuthenticationService extends IService<UserCertificationApproval> {
        void updateRecordById(Integer recordId, String note, Integer approvalResult);
}
