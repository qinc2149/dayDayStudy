package curatorDemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.shaded.com.google.common.util.concurrent.Service;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author qinc
 * @version V1.0
 * @Description: ${TODO}
 * @Date 2019/2/26 20:01
 */
public class ZKConnection02 {
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
        //6.创建节点
        String path="/hyq-c/c1";
        //创建一个零时节点，并且递归创建父节点
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path,"qinc".getBytes());
        //删除一个节点
        Stat stat= new Stat();
        byte[] data=client.getData().storingStatIn(stat).forPath(path);
        System.out.println(new String(data));
        client.delete()
                .deletingChildrenIfNeeded()
                .withVersion(stat.getVersion())
                .forPath(path);
        //更新数据
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/hyq-c/c2","qinc".getBytes());
        Stat stat1=client.setData().forPath("/hyq-c/c2");
        System.out.println(stat1);
        client.setData().withVersion(stat1.getVersion()).forPath("/hyq-c/c2","我正在学习zk".getBytes());
        System.out.println(new String(client.getData().forPath("/hyq-c/c2")));

    }

}
