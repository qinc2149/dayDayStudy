package curatorDemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.logging.SimpleFormatter;

/**
 * @author qinc
 * @version V1.0
 * @Description: Curator提供的分布式锁api
 * @Date 2019/2/28 11:47
 */
public class RecipesLock01 {
    private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
    static CuratorFramework client= CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(retryPolicy)
            .build();
    static final String lockPath="/lock-path";

    public static void main(String args[]){
        client.start();
        final InterProcessMutex lock = new InterProcessMutex(client,lockPath);
        for(int i=0;i<100;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.acquire();//获得
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderCode=sdf.format(new Date());
                    System.out.println("生成订单号："+orderCode);
                    try {
                        lock.release();//释放
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
