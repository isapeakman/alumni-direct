import request from "@/utils/request.js";

export function uploadFile(formData) {
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