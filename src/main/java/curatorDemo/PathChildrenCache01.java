
package curatorDemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author qinc
 * @version V1.0
 * @Description: ${TODO}
 * @Date 2019/2/26 20:01
 */
public class PathChildrenCache01 {
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
        String path="/qinc20-c";
        PathChildrenCache pathChildrenCache=new PathChildrenCache(client,path,true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()){
                    case CHILD_ADDED://新增子节点
                        System.out.println("CHILD_ADDED:"+event.getData().getPath());
                        break;
                    case CHILD_UPDATED://子节点数据变更
                        System.out.println("CHILD_UPDATED:"+event.getData().getPath());
                        break;
                    case CHILD_REMOVED://删除子节点
                        System.out.println("CHILD_REMOVED:"+event.getData().getPath());
                        break;
                    default:
                }

            }
        });
        client.create().withMode(CreateMode.PERSISTENT).forPath(path,"创建持久父节点".getBytes());

        client.create().creatingParentsIfNeeded().forPath(path+"/c1","我创建了一个子节点".getBytes());
        Thread.sleep(1000);
        client.setData().withVersion(-1).forPath(path+"/c1","我更新了子节点的数据".getBytes());
        Thread.sleep(1000);
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

}
