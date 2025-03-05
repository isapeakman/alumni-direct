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