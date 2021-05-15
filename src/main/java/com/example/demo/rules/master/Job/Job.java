package com.example.demo.rules.master.Job;

public class Job {
    public String id;

    //爬虫爬取的范围
    public int start;
    public int end;

    //分配任务的机器的id
    public int machine_id;

    public static final int STATUS_UNALLOCATED = 1;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FAILED = 2;

    //当前任务的状态
    public int status;

    public Job(int start, int end, int machine_id, int status){
        this.start = start;
        this.end = end;
        this.machine_id = machine_id;
        this.status = status;
    }
}
