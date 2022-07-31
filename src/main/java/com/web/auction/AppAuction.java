package com.web.auction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.web.auction.mapper")
public class AppAuction {

    public static void main(String[] args) {
        SpringApplication.run(AppAuction.class, args);
    }
}
