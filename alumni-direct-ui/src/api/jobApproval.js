import request from "@/utils/request.js";


export function getApprovals(approvalStatus, approvalResult, currentPage, pageSize) {
    // 构建参数对象，只包含有值的参数
    const params = {}
    if (approvalStatus) params.approvalStatus = approvalStatus
    if (approvalResult) params.approvalResult = approvalResult
    params.current = currentPage
    params.pageSize = pageSize
    return request({
        url: '/ad/jobApprovalRecord/all',
        method: 'get',
        params: params
    })
}

export function approveRecord(recordId, approvalStatus, note, approvalResult) {
    // 构建参数对象，只包含有值的参数
    const params = {}
    params.recordId = recordId
    if (note) params.note = note
    if (approvalStatus) params.approvalStatus = approvalStatus
    if (approvalResult) params.approvalResult = approvalResult
    return request({
        url: '/ad/jobApprovalRecord/judge',
        method: 'post',
        params: params
    })
}