package curatorDemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;

/**
 * @author qinc
 * @version V1.0
 * @Description: ${TODO}
 * @Date 2019/2/26 20:01
 */
public class ZKConnection01 {

    public static void  main(String args[]) throws Exception {

        //1重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        //2创建客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",
                5000,     //回话超时时间
                3000,     //连接超时时间
                retryPolicy
                );
        //3启动客户端
        client.start();

        //4Fluent风格api
        CuratorFramework client2 = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client2.start();
        //5,创建含有命名空间的回话
        CuratorFramework client3 = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .namespace("base")
                .build();
        client3.start();
        //6.创建节点
        String path="/hyq-c";
        //创建一个节点，初始内容为空
        client3.create().forPath(path);
        //创建一个节点，附带内容
        client3.create().forPath(path,"init".getBytes());
        //创建一个零时节点初始内容为空
        client3.create().withMode(CreateMode.EPHEMERAL).forPath(path);
        //创建一个零时节点，并且递归创建父节点
        client3.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);

    }

}
