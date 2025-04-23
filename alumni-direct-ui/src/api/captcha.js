import request from '@/utils/request'

export function sendCaptcha(email) {
    return request({
        url: '/ad/captcha/email',
        method: 'get',
        params: email,
    })
}
