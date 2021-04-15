package com.example.demo.rules.slave.Spider;

import com.google.gson.Gson;
import lombok.Data;

import javax.persistence.*;

//数据库实体
@Entity
@Table(name="WeiBoRecord")
@Data
public class SpiderContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name ="w_id")
    String w_id;

    @Column(name ="w_content")
    String w_content;

    @Column(name ="w_timestamp")
    long w_timestamp;

    @Column(name ="timestamp")
    long timestamp = 0;

    @Column(name ="w_content_id")
    String w_content_id = "";

    @Column(name ="type")
    int type = 0;

    @Column(name ="score")
    int score = 0;

    @Column(name = "isProcess")
    int isProcess;

    @Column(name = "score_content")
    int scoreContent;
}