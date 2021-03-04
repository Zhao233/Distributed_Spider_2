package com.example.demo.controller;

import com.example.demo.utils.zookeeper.lock.Imp.BaseDistributedLock;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class Conc {

    @Autowired
    BaseDistributedLock lock;

    @Autowired
    ZooKeeper zkClient;

    @RequestMapping(value = "/say",method = RequestMethod.GET)
    public String test() throws Exception {

        for(int i = 0; i < 5; i++){
            lock.acquire();

            int num = Integer.parseInt(new String( zkClient.getData("/test", true, null) ));

            System.out.println( num );

            num+=1;

            Stat stat = zkClient.setData("/test" , String.valueOf(num).getBytes(), -1);

            lock.release();
        }


        return "";
    }
}
