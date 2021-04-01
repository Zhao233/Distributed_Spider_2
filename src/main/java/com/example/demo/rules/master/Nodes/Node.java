package com.example.demo.rules.master.Nodes;

public class Node {
    public static final int ACTIVE = 0;
    public static final int DIED = 1;
    public static final int BUSSY = 2;
    public static final int FREE = 3;

    public String machine_id;

    public int status = ACTIVE;

    public int work_status = FREE;

    public Node(String machine_id){
        this.machine_id = machine_id;
    }

    public String getMachine_id() {
        return machine_id;
    }

    public void setMachine_id(String machine_id) {
        this.machine_id = machine_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getWork_status() {
        return work_status;
    }

    public void setWork_status(int work_status) {
        this.work_status = work_status;
    }
}
