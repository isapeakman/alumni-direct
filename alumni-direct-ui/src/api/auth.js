import request from '@/utils/request'

export function apply(formData) {
    return request({
        url: '/ad/auth/apply',
        method: 'post',
        data: formData,
    })
}

export function approveAuth(id, note, approvalResult) {
    return request({
        url: '/ad/auth/approve',
        method: 'post',
        params: {
            id,
            note,
            approvalResult,
        }
    })
}

export function getAuth() {
    return request({
        url: '/ad/auth/get',
        method: 'get',
    })
}

export function getApproval(current, pageSize) {
    return request({
        url: '/ad/auth/list',
        method: 'get',
        params: {
            current,
            pageSize,
        },
    })
}