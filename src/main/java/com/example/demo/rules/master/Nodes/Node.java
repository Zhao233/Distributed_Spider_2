package com.example.demo.rules.master.Nodes;

import lombok.Data;
import net.bytebuddy.dynamic.scaffold.MethodGraph;

import java.util.LinkedList;

@Data
public class Node {
    public static final int ACTIVE = 0;
    public static final int DIED = 1;
    public static final int BUSSY = 2;
    public static final int FREE = 3;

    public String machine_id;

    public int status = ACTIVE;

    public int work_status = FREE;

    public LinkedList<String> processing_job_id_list = new LinkedList<>();

    public Node(String machine_id){
        this.machine_id = machine_id;
    }

    public String getMachine_id() {
        return machine_id;
    }

    public void setMachine_id(String machine_id) {
        this.machine_id = machine_id;
    }

    public void add_processing_job_id(String id){
        this.processing_job_id_list.add(id);
    }

    public void remove_processing_job_id(String id){
        this.processing_job_id_list.remove(id);
    }

    public boolean is_processing_job_id_list_empty(){
        return processing_job_id_list.isEmpty();
    }
}
