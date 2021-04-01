package com.example.demo.rules.master.Job;

public class Job {
    public int id;

    public int start;
    public int end;

    public int machine_id;

    public Job(int start, int end){
        this.start = start;
        this.end = end;
    }
}
