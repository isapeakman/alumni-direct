import request from '@/utils/request'

// 获取职位卡片
export function getJobCard(currentPage, pageSize) {
    return request({
        url: '/ad/job/cards',
        method: 'get',
        params: {
            current: currentPage,
            pageSize: pageSize
        }
    })
}

// 搜索职位
export function searchJob({currentPage, pageSize, keyword, categoryId}) {
    // 构建参数对象，只包含有值的参数
    const params = {}

    if (currentPage) params.current = currentPage
    if (pageSize) params.pageSize = pageSize
    if (keyword) params.title = keyword
    if (categoryId) params.categoryId = categoryId

    return request({
        url: '/ad/job/cards',
        method: 'get',
        params: params
    })
}

// 获取职位详情
export function getJobDetail(jobId) {
    return request({
        url: '/ad/job/detail',
        method: 'get',
        params: {
            jobId: jobId
        }
    })
}

export function getJobById(status, currentPage, pageSize) {
    return request({
        url: '/ad/job/listById',
        method: 'get',
        params: {
            status: status,
            current: currentPage,
            pageSize: pageSize
        }
    })
}
