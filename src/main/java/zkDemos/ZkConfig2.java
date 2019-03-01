package zkDemos;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author qinc
 * @version V1.0
 * @Description: ${TODO}
 * @Date 2019/2/20 10:39
 */
public class ZkConfig2 implements Watcher {
    private static  CountDownLatch downLatch= new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("接收zk事件"+watchedEvent);
        if(Event.KeeperState.SyncConnected==watchedEvent.getState()){
            downLatch.countDown();
        }
    }

    public static void main(String args[]) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk= new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",500,new ZkConfig2());
        System.out.println("zk客户端是异步连接到服务端的："+zk.getState());
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("zk session established");
        //异步创建节点
        if (null==zk.exists("/zk-qinc", false)) {
            zk.create("/zk-qinc", "123".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
                        @Override
                        public void processResult(int rc, String path, Object ctx, String name) {
                            System.out.println("响应码："+rc+"\n"+"path:"+path+"\n"+"传入的参数："+ctx+"\n"+"节点名："+name);
                        }
                    }, "Hello zk!");
        }
        Thread.sleep(5000);
        //异步删除节点
        if(zk.getChildren("/zk-qinc",null).size()==0){
            zk.delete("/zk-qinc", 1, new AsyncCallback.VoidCallback(){
                @Override
                public void processResult(int rc, String path, Object ctx) {
                    System.out.println("响应码："+rc+"\n"+"path:"+path+"\n"+"传入的参数："+ctx);
                }
            }, "delete了一个节点");
        }
        Thread.sleep(50000);
    }
}