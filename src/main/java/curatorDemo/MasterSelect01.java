package curatorDemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author qinc
 * @version V1.0
 * @Description: curator提供的Master选举功能
 * @Date 2019/2/28 11:04
 */
public class MasterSelect01 {
    private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
    static CuratorFramework client= CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(retryPolicy)
            .build();
    static final String masterPath="/master-path";
    public static void main(String args[]) throws InterruptedException {
        client.start();//启动链接

        LeaderSelector selector= new LeaderSelector(
                client,
                masterPath,
                new LeaderSelectorListener(){
                    @Override
                    public void stateChanged(CuratorFramework curatorFramework, ConnectionState state) {
                        System.out.println(state);
                    }
                    @Override
                    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                        System.out.println("成为Master角色！");
                        Thread.sleep(3000);
                        System.out.println("完成Master的任务");
                    }
                }
        );
        selector.autoRequeue();
        selector.start();
        Thread.sleep(Integer.MAX_VALUE);

  }
}
