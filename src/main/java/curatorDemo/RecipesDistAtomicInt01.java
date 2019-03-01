
package curatorDemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qinc
 * @version V1.0
 * @Description: Curator提供的分布式计数器
 * @Date 2019/2/28 11:47
 */
public class RecipesDistAtomicInt01 {
    private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
    static CuratorFramework client= CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(retryPolicy)
            .build();
    static final String distPath="/dist-path";

    public static void main(String args[]) throws Exception {
        client.start();
        DistributedAtomicInteger distInt= new DistributedAtomicInteger(
                client,
                distPath,
                new RetryNTimes(3,1000)
        );
        AtomicValue<Integer> rc= distInt.add(8);
        System.out.println(rc.succeeded());
    }
}
