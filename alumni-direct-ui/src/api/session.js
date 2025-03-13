import request from '@/utils/request'

export function getSessionList() {
    return request(
        {
            url: '/ad/chat/session/list',
            method: 'get',
        }
    )
}

export function getSessionHistory(sessionId, historyTime) {
    return request(
        {
            url: '/ad/chat/session/history',
            method: 'get',
            params: {
                sessionId: sessionId,
                historyTime: historyTime
            }
        }
    )
}