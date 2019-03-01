
package curatorDemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qinc
 * @version V1.0
 * @Description: 相当于watcher
 * @Date 2019/2/26 20:01
 */
public class NodeCache01 {
    //1重试策略
    private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
    //2创建客户端
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(retryPolicy)
            .build();


    public static void  main(String args[]) throws Exception {
        client.start();
        //6.异步创建节点
        String path="/hyq-c/c1";

        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback(){
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("type："+curatorEvent.getType()+"\n"+"Code:"+curatorEvent.getResultCode());
                System.out.println("Thread:"+ Thread.currentThread().getName());
            }
        }).forPath(path,"我在学java".getBytes());
        final NodeCache cache= new NodeCache(client,path,false);
        cache.start(true);
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("node Data changed "+new String(client.getData().forPath(path)));
            }
        });
        client.setData().withVersion(-1).forPath(path,"我在学go".getBytes());
        Thread.sleep(1000);
        client.delete().deletingChildrenIfNeeded().forPath(path);//删除节点不会触发事件监听
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,"创建新节点！".getBytes());//创建新节点会触发事件监听
        Thread.sleep(Integer.MAX_VALUE);
        System.out.println("end");
    }

}
