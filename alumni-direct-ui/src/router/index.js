import {createRouter, createWebHistory} from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
    {
        path: '/',
        name: 'Home',
        component: () => import('../views/home/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('../views/dashboard/index.vue'),
                meta: {title: '控制台', icon: 'dashboard'}
            },
            {
                path: 'recommend',
                name: 'Recommend',
                component: () => import('../views/recommend/index.vue'),
                meta: {title: '推荐职位', icon: 'recommend'}
            }
        ]
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
