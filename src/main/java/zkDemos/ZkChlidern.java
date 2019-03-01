package zkDemos;

import org.apache.zookeeper.*;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author qinc
 * @version V1.0
 * @Description: ${TODO}
 * @Date 2019/2/20 10:39
 */
public class ZkChlidern implements Watcher {
    private static  CountDownLatch downLatch= new CountDownLatch(1);
    private static ZooKeeper zk=null;

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("接收zk事件"+watchedEvent);
        if(Event.KeeperState.SyncConnected==watchedEvent.getState()){
            if (Event.EventType.None==watchedEvent.getType()&&null==watchedEvent.getPath()){
                downLatch.countDown();
            }else if( Event.EventType.NodeChildrenChanged==watchedEvent.getType()){
                try {
                    List<String> zkList=zk.getChildren(watchedEvent.getPath(),true);
                    System.out.println("zkChildren= "+zkList);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String args[]) throws IOException, KeeperException, InterruptedException {
        zk= new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",500,new ZkChlidern());
        System.out.println("zk客户端是异步连接到服务端的："+zk.getState());
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("zk session established");
        //创建父节点（持久节点）
        String path="/zk-qinc";
        if (null==zk.exists(path, false)) {
            System.out.println("创建节点："+path);
            path=zk.create(path,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
        if (null==zk.exists(path+"/c1", false)) {
            zk.create(path+"/c1", "12333".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
                        @Override
                        public void processResult(int rc, String path, Object ctx, String name) {
                            System.out.println("响应码："+rc+"\n"+"path:"+path+"\n"+"传入的参数："+ctx+"\n"+"节点名："+name);
                        }
                    }, "Hello zk!");
        }
        Thread.sleep(5000);
        List<String> zkChlidrenList= zk.getChildren(path,true);
        System.out.println(zkChlidrenList);
        //创建节点c2
        if (null==zk.exists(path+"/c2", false)) {
            zk.create(path+"/c2", "12333".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
                        @Override
                        public void processResult(int rc, String path, Object ctx, String name) {
                            System.out.println("响应码："+rc+"\n"+"path:"+path+"\n"+"传入的参数："+ctx+"\n"+"节点名："+name);
                        }
                    }, "Hello zk!");
        }
        Thread.sleep(5000);
    }
}