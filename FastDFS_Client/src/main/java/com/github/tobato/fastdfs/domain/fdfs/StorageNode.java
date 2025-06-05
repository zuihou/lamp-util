package com.github.tobato.fastdfs.domain.fdfs;

import com.github.tobato.fastdfs.domain.proto.OtherConstants;
import com.github.tobato.fastdfs.domain.proto.mapper.FdfsColumn;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

/**
 * 向tracker请求上传、下载文件或其他文件请求时，tracker返回的文件storage节点的信息
 *
 * @author yuqih
 */
@Setter
@Getter
public class StorageNode {

    @FdfsColumn(index = 0, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private String groupName;
    @FdfsColumn(index = 1, max = OtherConstants.FDFS_IPADDR_SIZE - 1)
    private String ip;
    @FdfsColumn(index = 2)
    private int port;
    /**
     *  the storeIndex
     */
    @FdfsColumn(index = 3)
    private byte storeIndex;

    /**
     * 存储节点
     *
     * @param ip IP
     * @param port 端口
     * @param storeIndex 索引
     */
    public StorageNode(String ip, int port, byte storeIndex) {
        super();
        this.ip = ip;
        this.port = port;
        this.storeIndex = storeIndex;
    }

    public StorageNode() {
        super();
    }

    /**
     * @return the inetSocketAddress
     */
    public InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(ip, port);
    }

    @Override
    public String toString() {
        return "StorageClient [groupName=" + groupName + ", ip=" + ip + ", port=" + port + ", storeIndex=" + storeIndex
               + "]";
    }

}
