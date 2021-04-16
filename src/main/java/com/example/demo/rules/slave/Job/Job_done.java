package com.example.demo.rules.slave.Job;

import com.example.demo.rules.slave.Spider.Spider;
import com.example.demo.rules.slave.Spider.SpiderContent;

import java.util.List;

//已完成的任务
public class Job_done {
    public Job job;

    public List<SpiderContent> contents;

    public Job_done(Job job, List<SpiderContent> contents) {

        this.contents = contents;
    }

    public List<SpiderContent> getContents() {
        return contents;
    }

    public void setContents(List<SpiderContent> contents) {
        this.contents = contents;
    }
}
