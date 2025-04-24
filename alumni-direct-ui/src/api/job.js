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

export function getRecommendJobCard(currentPage, pageSize) {
    return request({
        url: '/ad/job/recommend/cards',
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

export function getCategories(status, currentPage, pageSize) {
    return request({
        url: '/ad/category/getTree',
        method: 'get',
    })
}

// 添加职位
export function addJob(formData) {
    return request({
        url: '/ad/job/add',
        method: 'post',
        params: formData
    })
}

// 发布职位
export function publish(jobId) {
    return request({
        url: '/ad/job/publish',
        method: 'post',
        params: {
            jobId: jobId
        }
    })
}

// 更新职位
export function update(job) {
    return request({
        url: '/ad/job/update',
        method: 'post',
        params: job
    })
}

// 关闭职位
export function closeJobById(jobId) {
    return request({
        url: '/ad/job/close',
        method: 'get',
        params: {
            jobId: jobId
        }
    })
}

// 取消发布
export function cancel(jobId) {
    return request({
        url: '/ad/job/offline',
        method: 'get',
        params: {
            jobId: jobId
        }
    })
}

// 获取当前用户职位申请记录
export function getJobApplyRecord(currentPage, pageSize) {
    return request({
        url: '/ad/job/apply/get',
        method: 'get',
        params: {
            current: currentPage,
            pageSize: pageSize
        }
    })
}