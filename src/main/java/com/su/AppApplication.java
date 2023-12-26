package com.su;

import com.su.annotation.LoadFile;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author swt
 * @date 2023/7/16 16:52
 */
@SpringBootApplication
@MapperScan("com.su.mapper")
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class,args);
    }


}
