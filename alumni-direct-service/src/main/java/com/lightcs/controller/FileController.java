package com.lightcs.controller;

import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.utils.MinioUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-20
 * @Description:
 * @Version: 1.0
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private MinioUtils minioUtils;

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public BaseResponse<String> upload(@RequestPart MultipartFile file) {
        // 生成唯一的文件名
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        minioUtils.upload(fileName, file);
        // 暂时写死
        return ResultBuilder.success("http://192.168.110.157:9000/bucket/" + fileName);
    }

    @Operation(summary = "上传简历")
    @PostMapping("/upload/resume")
    public BaseResponse<String> uploadResume(@RequestPart MultipartFile file) {
        // 生成唯一的文件名
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        minioUtils.upload(fileName, file);
        // 暂时写死
        return ResultBuilder.success(fileName);
    }


    @Operation(summary = "下载文件")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(String fileName) {
        return minioUtils.download(fileName);
    }
}
