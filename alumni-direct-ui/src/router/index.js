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
                component: () => import('@/views/user/job-recommend.vue'),
                meta: {title: '推荐职位', icon: 'recommend'}
            },
            {
                path: 'personalCenter',
                name: 'PersonalCenter',
                component: () => import('@/views/user/person-center.vue'),
            },
            {
                path: 'apply',
                name: 'Apply',
                component: () => import('@/views/user/job-apply-record.vue'),
            },
            {
                path: 'fair',
                name: 'fair',
                component: () => import('../views/user/job-fair.vue'),
            },
            {
                path: 'chat',
                name: 'Chat',
                component: () => import('../views/chat.vue'),
            }, {
                path: 'search',
                name: 'Search',
                component: () => import('@/views/user/job-search.vue'),
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
                component: () => import('@/views/recruiter/job-manage.vue')
            },
            {
                path: 'auth',
                name: 'Auth',
                component: () => import('../views/recruiter/alumni-auth.vue'),
            },
            {
                path: 'auth/success',
                name: 'AuthSuccess',
                component: () => import('../views/recruiter/alumni-auth-success.vue'),
            },
            {
                path: 'chat',
                name: 'RChat',
                component: () => import('../views/chat.vue'),
            },
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
                component: () => import('@/views/admin/manage-user.vue'),
            },
            {
                path: 'approval',
                name: 'Approval',
                component: () => import('@/views/admin/manage-job.vue')
            },
            {
                path: 'auth',
                name: 'AuthApproval',
                component: () => import('@/views/admin/manage-auth.vue')
            },
            {
                path: 'fair',
                name: 'Fair',
                component: () => import('../views/admin/manage-fair.vue'),
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
