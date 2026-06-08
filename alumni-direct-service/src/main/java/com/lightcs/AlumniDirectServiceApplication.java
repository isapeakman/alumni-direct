package com.lightcs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.lightcs.mapper")
@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
public class AlumniDirectServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlumniDirectServiceApplication.class, args);
    }

}
