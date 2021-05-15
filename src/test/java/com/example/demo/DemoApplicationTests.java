package com.example.demo;

import com.example.demo.rules.slave.Spider.Spider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    public Spider spider;

    @Test
    void contextLoads() throws IOException, InterruptedException {
        spider.crawling_data(0,2);
    }

}
