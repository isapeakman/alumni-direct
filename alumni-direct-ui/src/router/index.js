import {createRouter, createWebHistory} from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/login/index.vue')
    },
    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('../views/dashboard/index.vue'),
                meta: {title: '控制台', icon: 'dashboard'}
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
