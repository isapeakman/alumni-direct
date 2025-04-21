import request from "@/utils/request.js";

export function uploadFile(formData) {
    return request({
        url: '/ad/file/upload',
        method: 'post',
        data: formData
    })
}