package com.example.demo.utils.zookeeper.lock.Imp;

import com.example.demo.utils.zookeeper.ZookeeperConfig;
import com.example.demo.utils.zookeeper.lock.DistributedLock;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service("baseDistributedLock")
public class BaseDistributedLock implements DistributedLock {

     /** ZkClient客户端 */
     @Autowired
     private ZooKeeper zkClient;


     /** /locker节点路径 */
     @Value("${zookeeper.lock_path}")
     private String lockerNodePath;
     private ThreadLocal<String> nodeId = new ThreadLocal<>();

    byte[] data= {1};


    @Override
     public void acquire() throws Exception {

         try {
             Thread.sleep(300);
             // 创建临时子节点
             String myNode = zkClient.create(lockerNodePath + "/" + ZookeeperConfig.machine_id , data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL_SEQUENTIAL);

             System.out.println(ZookeeperConfig.machine_id + ": " + "created");

                // 取出所有子节点
             List<String> subNodes = zkClient.getChildren(lockerNodePath, false);
             TreeSet<String> sortedNodes = new TreeSet<>();

             for(String node :subNodes) {
                 sortedNodes.add( lockerNodePath + "/" + node );
             }

             String smallNode = sortedNodes.first();
             String preNode = sortedNodes.lower(myNode);

             if (myNode.equals( smallNode )) {
                    // 如果是最小的节点,则表示取得锁
                 System.out.println(ZookeeperConfig.machine_id + ": " + "get lock");
                 this.nodeId.set(myNode);
                 return;
             }

             CountDownLatch latch = new CountDownLatch(1);
             Stat stat = zkClient.exists(preNode, new LockWatcher(latch));// 同时注册监听。

             // 判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
             if (stat != null) {
                 System.out.println("machine_id: " + ZookeeperConfig.machine_id +
                            " waiting for " + preNode + " released lock");

                 latch.await();// 等待，这里应该一直等待其他线程释放锁
                 nodeId.set(myNode);
                 latch = null;
             }
         } catch (Exception e) {
                throw new RuntimeException(e);
         }
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return false;
    }

    @Override
    public void release() throws Exception {
        try {
            System.out.println("machined_id: " + ZookeeperConfig.machine_id + " unlock ");
            if (null != nodeId) {
                zkClient.delete(nodeId.get(), -1);
            }
            nodeId.remove();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}

class LockWatcher implements Watcher {
    private CountDownLatch latch = null;

    public LockWatcher(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void process(WatchedEvent event) {

        if (event.getType() == Event.EventType.NodeDeleted)
            latch.countDown();
    }
}
