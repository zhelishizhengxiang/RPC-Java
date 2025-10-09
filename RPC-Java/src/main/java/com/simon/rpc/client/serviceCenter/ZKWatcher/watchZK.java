package com.simon.rpc.client.serviceCenter.ZKWatcher;

import com.simon.rpc.client.cache.ServiceCache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.serviceCenter.ZKWatcher
 * @Description:
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
public class watchZK {
    //curator 提供的zookeeper客户端
    private CuratorFramework client;
    //本地缓存
    private ServiceCache cache;

    public watchZK(CuratorFramework client, ServiceCache cache){
        this.client=client;
        this.cache=cache;
    }

    /**
     * 监听当前节点和子节点的 更新，创建，删除
     * @param path 这里没用到因为client在定义namespace的时候已经指定了路径
     */
    public void watchChange(String path) throws InterruptedException {
        //curator5引出的统一watcher用于替代curator4中的三个watcher，
        // 对于watcher的分类放在了CuratorCacheListener中
        // 如果使用他们的默认实现，就可以监听指定路径下的本节点+所有子节点
        //并且该client指定了namespace，此时的的路径其实是/MyRPC
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                // 第一个参数：事件类型（枚举）
                // 第二个参数：节点更新前的状态和数据
                // 第三个参数：节点更新后的状态和数据
                // 创建节点时：节点刚被创建，不存在 更新前节点 ，所以第二个参数为 null
                // 删除节点时：节点被删除，不存在 更新后节点 ，所以第三个参数为 null
                // 节点创建时没有赋予值 create /curator/app1 只创建节点，在这种情况下，更新前节点的 data 为 null，获取不到更新前节点的数据
                switch (type.name()) {
                    case "NODE_CREATED": // 监听器第一次执行时节点存在也会触发次事件
                        String[] pathList= pasrePath(childData1);
                        if(pathList.length<=2)
                            break;
                        else {
                            String serviceName=pathList[1];
                            String address=pathList[2];
                            //将新注册的服务加入到本地缓存中
                            cache.addServiceToCache(serviceName,address);
                            break;
                        }

                    case "NODE_CHANGED": // 节点更新
                        if (childData.getData() != null) {
                            System.out.println("修改前的数据: " + new String(childData.getData()));
                        } else {
                            System.out.println("节点第一次赋值!");
                        }
                        String[] oldPathList=pasrePath(childData);
                        String[] newPathList=pasrePath(childData1);
                        cache.replaceServiceAddress(oldPathList[1],oldPathList[2],newPathList[2]);
                        System.out.println("修改后的数据: " + new String(childData1.getData()));
                        break;

                    case "NODE_DELETED": // 节点删除
                        String[] pathList_d= pasrePath(childData);
                        if(pathList_d.length<=2) break;
                        else {
                            String serviceName=pathList_d[1];
                            String address=pathList_d[2];
                            //将服务从本地缓存中删除
                            cache.deleteServiceFromCache(serviceName,address);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //开启监听
        curatorCache.start();
    }
    //解析节点对应地址
    public String[] pasrePath(ChildData childData){
        //获取更新的节点的路径
        String path=new String(childData.getPath());
        //按照格式 ，读取
        return path.split("/");
    }
}
