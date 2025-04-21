package com.lightcs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-20
 * @Description:
 * @Version: 1.0
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*", originPatterns = "*")
public class FileController {
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public String upload(@RequestPart MultipartFile file) {
        return "http://localhost:8080/ad/static/avator2.png";
    }
}
