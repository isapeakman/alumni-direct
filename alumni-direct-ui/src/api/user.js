import request from '@/utils/request'

export function login(data) {
    return request(
        {
            url: '/ad/user/login',
            method: 'post',
            params: data
        }
    )
}

export function register(data) {
    return request(
        {
            url: '/ad/user/register',
            method: 'post',
            params: data
        }
    )
}

// 分页获取所有用户信息
export function fetchUserData(currentPage, pageSize, username) {
    // 构建参数对象，只包含有值的参数
    const params = {}
    if (username) params.username = username
    params.current = currentPage
    params.pageSize = pageSize
    return request(
        {
            url: '/ad/user/list',
            method: 'get',
            params: params
        }
    )
}

export function getUserInfo(userId) {
    return request(
        {
            url: '/ad/user/current',
            method: 'get',
            params: userId
        }
    )
}
// 修改头像
export function updateAvatar() {
    // 构建参数对象，只包含有值的参数
    const params = {}
    if (username) params.username = username
    params.current = currentPage
    params.pageSize = pageSize
    return request(
        {
            url: '/ad/user/list',
            method: 'get',
            params: params
        }
    )
}

// 修改用户信息
export function update(formData) {
    // 构建参数对象，只包含有值的参数
    return request(
        {
            url: '/ad/user/update',
            method: 'post',
            data: formData
        }
    )
}

// 获取用户的意向信息
export function getIntention() {
    return request(
        {
            url: '/ad/user/intention/get',
            method: 'get',
        }
    )
}

// 保存用户的意向信息
export function saveIntention(formData) {
    // 构建参数对象，只包含有值的参数
    return request(
        {
            url: '/ad/user/intention/save',
            method: 'post',
            data: formData
        }
    )
}