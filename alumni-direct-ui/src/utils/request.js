import axios from 'axios'

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 添加固定的 Authorization 头
        config.headers['Authorization'] = 'QV0mjV688hQT-bZ2pfvOB4HSgVS6lfkQ'
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        return response
    },
    error => {
        return Promise.reject(error)
    }
)

export default request 