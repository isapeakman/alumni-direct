import request from "@/utils/request.js";

export function uploadFile(formData) {
    return request({
        url: '/ad/file/upload',
        method: 'post',
        data: formData
    })
}

export function uploadResume(formData) {
    return request({
        url: '/ad/file/upload',
        method: 'post',
        data: formData
    })
}

export function downloadFile(fileName) {
    return request({
        url: '/ad/file/download',
        method: 'get',
        params: {fileName},
        responseType: 'blob'
    })
}

export function parseResume(formData) {
    return request({
        url: '/ad/ai/resume/parse',
        method: 'post',
        data: formData
    })
}