/*
 * Project Ren @ 2018
 * Rinkako, Ariana, Gordan. SYSU SDCS.
 */
package org.sysu.renResourcing.consistency;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

/**
 * Author: Rinkako
 * Date  : 2018/4/6
 * Usage : Cluster management by ZooKeeper.
 */
public class ZKCluster implements IClusterManager {

    /**
     * Get the global instance of ZKCluster manager.
     * This method is thread SAFE.
     * @return ZKCluster instance.
     */
    public static ZKCluster GetInstance() {
        if (ZKCluster.syncObj == null) {
            synchronized (ZKCluster.class) {
                if (ZKCluster.syncObj == null) {
                    return ZKCluster.syncObj = new ZKCluster();
                }
            }
        }
        return ZKCluster.syncObj;
    }

    /**
     * Write a string data to a cluster global data node.
     *
     * @param path    global data path
     * @param content content of node
     */
    @Override
    public void WriteNodeByString(String path, String content) throws Exception {
        this.WriteNode(path, content.getBytes());
    }

    /**
     * Read a string data from a cluster global data node.
     *
     * @param path global data path
     * @return content of node
     */
    @Override
    public String ReadNodeByString(String path) throws Exception {
        return new String(this.ReadNode(path));
    }

    /**
     * Write data to a cluster global data node.
     *
     * @param path    global data path
     * @param content content of node
     */
    @Override
    public void WriteNode(String path, byte[] content) throws Exception {
        this.ZClient.newNamespaceAwareEnsurePath(path).ensure(this.ZClient.getZookeeperClient());
        this.ZClient.setData().forPath(path, content);
    }

    /**
     * Read data from a cluster global data node.
     *
     * @param path global data path
     * @return content of node
     */
    @Override
    public byte[] ReadNode(String path) throws Exception {
        return this.ZClient.getData().forPath(path);
    }

    /**
     * Get all children path of a specific data node.
     *
     * @param path global data path
     * @return a List of string of children path
     */
    @Override
    public List<String> GetChildren(String path) throws Exception {
        return this.ZClient.getChildren().forPath(path);
    }

    /**
     * Remove a data node from cluster.
     *
     * @param path global data path
     */
    @Override
    public void RemoveNode(String path) throws Exception {
        this.ZClient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
    }

    /**
     * Check if a data node exists in cluster.
     *
     * @param path global data path
     * @return true if node exist
     */
    @Override
    public boolean Contains(String path) throws Exception {
        return this.ZClient.checkExists().forPath(path) != null;
    }

    /**
     * Lock a global node path.
     *
     * @param path global data path
     */
    @Override
    public void Lock(String path) {
        // todo
    }

    /**
     * Try to lock a global node path.
     *
     * @param path global data path
     * @return true if get locked by this method
     */
    @Override
    public boolean TryLock(String path) {
        // todo
        return false;
    }

    /**
     * Unlock a global node path.
     *
     * @param path global data path
     */
    @Override
    public void Unlock(String path) {
        // todo
    }

    /**
     * Private constructor for preventing created outside.
     */
    private ZKCluster() {
        RetryPolicy rp = new ExponentialBackoffRetry(1000, 3);
        Builder builder = CuratorFrameworkFactory.builder()
                .connectString(ZKCluster.ConnectZKAddress)
                .connectionTimeoutMs(ZKCluster.TimeOut)
                .sessionTimeoutMs(ZKCluster.TimeOut)
                .retryPolicy(rp)
                .namespace(ZKCluster.NameSpace);
        this.ZClient = builder.build();
        this.ZClient.start();
        this.ZClient.newNamespaceAwareEnsurePath(ZKCluster.NameSpace);
    }

    /**
     * ZooKeeper Client.
     */
    private CuratorFramework ZClient;

    /**
     * [Configurable]
     * ZooKeeper server address. split by `,`.
     */
    private static final String ConnectZKAddress = "127.0.0.1:2181";

    /**
     * [Configurable]
     * ZooKeeper timeout.
     */
    private static final Integer TimeOut = 5000;

    /**
     * Global static instance.
     */
    private static ZKCluster syncObj = null;
}