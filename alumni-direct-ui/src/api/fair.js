import request from '@/utils/request'

export function getFairCards(currentPage, pageSize) {
    return request({
        url: '/ad/jobFair/cards',
        method: 'get',
        params: {
            current: currentPage,
            pageSize: pageSize
        }
    })
}

export function getFairList(currentPage, pageSize) {
    return request({
        url: '/ad/jobFair/list',
        method: 'get',
        params: {
            current: currentPage,
            pageSize: pageSize
        }
    })
}

export function addFair(formData) {
    return request({
        url: '/ad/jobFair/add',
        method: 'post',
        data: formData
    })
}

export function updateFair(formData) {
    return request({
        url: '/ad/jobFair/update',
        method: 'post',
        data: formData
    })
}

export function deleteFair(id) {
    return request({
        url: '/ad/jobFair/delete/' + id,
        method: 'post',
    })
}