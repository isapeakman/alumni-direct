package com.lightcs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lightcs.mapper")
public class AlumniDirectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlumniDirectApplication.class, args);
	}

}
