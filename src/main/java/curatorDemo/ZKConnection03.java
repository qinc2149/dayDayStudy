
package curatorDemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qinc
 * @version V1.0
 * @Description: ${TODO}
 * @Date 2019/2/26 20:01
 */
public class ZKConnection03 {
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
        ExecutorService executor= Executors.newFixedThreadPool(2);
        CountDownLatch downLatch= new CountDownLatch(2);
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback(){
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("type："+curatorEvent.getType()+"\n"+"Code:"+curatorEvent.getResultCode());
                System.out.println("Thread:"+ Thread.currentThread().getName());
                downLatch.countDown();
            }
        }).forPath(path);
        //
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback(){
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("type："+curatorEvent.getType()+"\n"+"Code:"+curatorEvent.getResultCode());
                System.out.println("Thread:"+ Thread.currentThread().getName());
                downLatch.countDown();
            }
        },executor).forPath(path);
        downLatch.await();
        executor.shutdown();
        System.out.println("end");
    }

}
