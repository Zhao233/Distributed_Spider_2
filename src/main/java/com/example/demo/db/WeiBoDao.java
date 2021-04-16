package com.example.demo.db;

import com.example.demo.rules.slave.Spider.SpiderContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//数据库操作层
@Repository
public interface WeiBoDao extends JpaRepository<SpiderContent, Long> {
//    @Query(nativeQuery = true, value = "SELECT w_timestamp from wei_bo_record where type=0 order by w_timestamp desc limit 1")
//    long getLastedTime();
}
