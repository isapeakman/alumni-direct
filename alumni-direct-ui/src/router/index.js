import {createRouter, createWebHistory} from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/user/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/user/main.vue'),
                meta: {title: '控制台', icon: 'dashboard'}
            },
            {
                path: 'recommend',
                name: 'Recommend',
                component: () => import('@/views/user/recommend/index.vue'),
                meta: {title: '推荐职位', icon: 'recommend'}
            },
            {
                path: 'personalCenter',
                name: 'PersonalCenter',
                component: () => import('@/views/user/personalCenter.vue'),
            }
        ]
    },
    {
        path: '/recruitment',
        name: 'recruitment',
        component: () => import('@/views/recruiter/index.vue'),
        redirect: '/recruitment/manage',
        children: [
            // {
            //     path: 'recruiter',
            //     name: 'Recruiter',
            //     component: () => import('@/views/recruiter/home/main.vue'),
            //     meta: {title: '控制台', icon: 'recruiter'}
            // },
            {
                path: 'manage',
                name: 'Manage',
                component: () => import('@/views/recruiter/manage/index.vue')
            }
        ]
    },
    // 管理员
    {
        path: '/admin',
        name: 'admin',
        component: () => import('@/views/admin/index.vue'),
        redirect: '/admin/user',
        children: [
            {
                path: 'user',
                name: 'User',
                component: () => import('@/views/admin/user.vue'),
            },
            {
                path: 'approval',
                name: 'Approval',
                component: () => import('@/views/admin/approval.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
