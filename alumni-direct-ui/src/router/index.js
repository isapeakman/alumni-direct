import {createRouter, createWebHistory} from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/user/home/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/user/dashboard/index.vue'),
                meta: {title: '控制台', icon: 'dashboard'}
            },
            {
                path: 'recommend',
                name: 'Recommend',
                component: () => import('@/views/user/recommend/index.vue'),
                meta: {title: '推荐职位', icon: 'recommend'}
            }
        ]
    },
    {
        path: '/recruitment',
        name: 'recruitment',
        component: () => import('@/views/recruiter/home/index.vue'),
        redirect: '/recruitment/recruiter',
        children: [
            {
                path: 'recruiter',
                name: 'Recruiter',
                component: () => import('@/views/recruiter/home/index.vue'),
                meta: {title: '控制台', icon: 'recruiter'}
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
