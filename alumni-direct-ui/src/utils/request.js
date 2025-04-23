import axios from 'axios'

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 添加 Authorization 头
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        console.log(response)
        return response
    },
    error => {// 当服务器将会话缓存清除，前端仍携带 token 访问接口时，服务器会返回 401 错误
        // 检查是否是 401 错误
        if (error.response && error.response.status === 401) {
            // 检查错误信息是否表明 token 无效
            if (error.response.data && error.response.data.message === "Token is invalid") {
                // 清除 localStorage 中的 token 和用户信息
                localStorage.removeItem('token')
                localStorage.removeItem('userInfo')

                // 可选：跳转到登录页面
                // 注意：这里需要确保你有访问路由的能力
                // 如果是在 Vue 组件外使用，可能需要导入 router 实例
                // window.location.href = '/login' 或者使用路由跳转
                if (window.location.pathname !== '/dashboard') {
                    window.location.href = '/dashboard'
                }
            }
        }

        return Promise.reject(error)
    }
)

export default request
