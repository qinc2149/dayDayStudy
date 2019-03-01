

package zkDemos;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author qinc
 * @version V1.0
 * @Description: ${TODO}
 * @Date 2019/2/20 10:39
 */
public class ZkSetData implements Watcher {
    private static  CountDownLatch downLatch= new CountDownLatch(1);
    private static ZooKeeper zk=null;

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("接收zk事件"+watchedEvent);
        if(Event.KeeperState.SyncConnected==watchedEvent.getState()){
            if (Event.EventType.None==watchedEvent.getType()&&null==watchedEvent.getPath()){
                downLatch.countDown();
            }else if( Event.EventType.NodeDataChanged==watchedEvent.getType()){
                Stat stat = new Stat();
                try {
                    byte[] data = zk.getData("/zk-qinc/c1",true,stat);
                    System.out.println(new String(data));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public static void main(String args[]) throws IOException, KeeperException, InterruptedException {
        zk= new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",500,new ZkSetData());
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
        //-1表示按当前数据的最新版本更新，对原子操作要求不高的可以用-1
        Stat stat=zk.setData(path+"/c1","齐宁超".getBytes(),-1);
        System.out.println(stat.toString());
        Stat stat1=zk.setData(path+"/c1","小明".getBytes(),stat.getVersion());
        System.out.println(stat1.toString());
        Stat stat2=zk.setData(path+"/c1","小黄".getBytes(),stat.getVersion());
        System.out.println(stat2.toString());
    }
}