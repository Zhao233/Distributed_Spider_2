package com.example.demo.rules.master.Nodes;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class NodeManagement {
    @Autowired
    ZooKeeper zkClient;

    @Value("${zookeeper.nodes_path}")
    public String nodes_path;

    List<Node> nodes = null;

    public LinkedList<Node> get_all_slaves() throws KeeperException, InterruptedException {
        List<String> child_list = zkClient.getChildren(nodes_path,null);
        LinkedList<Node> nodes = new LinkedList<>();

         for (String temp : child_list){
             byte[] data = zkClient.getData(nodes_path+"/"+temp,null, null);

             String data_ = new String(data);
             if(data_.equals("slave")){
                 nodes.add(new Node(temp));
             }
         }

         return nodes;
    }

    public boolean is_slave_alive(String machine_id) throws KeeperException, InterruptedException {
        return zkClient.exists(nodes_path+"/"+machine_id, null) != null;
    }

    public void get_one_machine(){
    }
}
