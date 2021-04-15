package com.example.demo.rules.slave.Job;

import com.example.demo.rules.slave.Spider.Spider;
import com.example.demo.rules.slave.Spider.SpiderContent;

import java.util.List;

//已完成的任务
public class Job_done {
    //爬虫爬取的范围
    public int start;
    public int end;

    //分配任务的机器的id
    public int machine_id;

    public List<SpiderContent> contents;

    public Job_done(Job job, List<SpiderContent> contents) {
        this.start = job.start;
        this.end = job.end;
        this.machine_id = job.machine_id;
        this.contents = contents;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getMachine_id() {
        return machine_id;
    }

    public void setMachine_id(int machine_id) {
        this.machine_id = machine_id;
    }

    public List<SpiderContent> getContents() {
        return contents;
    }

    public void setContents(List<SpiderContent> contents) {
        this.contents = contents;
    }
}
