package zkDemos;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author qinc
 * @version V1.0
 * @Description: ${TODO}
 * @Date 2019/2/20 10:39
 */
public class ZkConfig implements Watcher {
    private static  CountDownLatch downLatch= new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("接收zk事件"+watchedEvent);
        if(Event.KeeperState.SyncConnected==watchedEvent.getState()){
            downLatch.countDown();
        }
    }

    public static void main(String args[]) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk= new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",500,new ZkConfig());
        System.out.println("zk客户端是异步连接到服务端的："+zk.getState());
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("zk session established");
        String path = null;
        if (null==zk.exists("/zk-qinc", false)) {
            path=zk.create("/zk-qinc","123".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
            System.out.println("创建临时节点："+path);
        }
        String path1 = null;
        if (null==zk.exists("/zk-Hyq", false)) {
             path1=zk.create("/zk-Hyq","456".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("创建临时顺序节点："+path1);
        }
       byte[] pathData=zk.getData(path,null,null);
        System.out.println("pathData:"+new String(pathData));
        byte[] pathData2=zk.getData(path1,null,null);
        System.out.println("pathData:"+new String(pathData2));
    }
}
