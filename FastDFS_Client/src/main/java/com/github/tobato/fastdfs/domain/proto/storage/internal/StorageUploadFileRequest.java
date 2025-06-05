package com.github.tobato.fastdfs.domain.proto.storage.internal;

import com.github.tobato.fastdfs.domain.proto.CmdConstants;
import com.github.tobato.fastdfs.domain.proto.FdfsRequest;
import com.github.tobato.fastdfs.domain.proto.OtherConstants;
import com.github.tobato.fastdfs.domain.proto.ProtoHead;
import com.github.tobato.fastdfs.domain.proto.mapper.FdfsColumn;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * 文件上传命令
 *
 * @author tobato
 */
@Getter
@Setter
public class StorageUploadFileRequest extends FdfsRequest {

    private static final byte UPLOAD_CMD = CmdConstants.STORAGE_PROTO_CMD_UPLOAD_FILE;
    private static final byte UPLOAD_APPENDER_CMD = CmdConstants.STORAGE_PROTO_CMD_UPLOAD_APPENDER_FILE;

    /**
     * 存储节点index
     */
    @FdfsColumn(index = 0)
    private byte storeIndex;
    /**
     * 发送文件长度
     */
    @FdfsColumn(index = 1)
    private final long fileSize;
    /**
     * 文件扩展名
     */
    @FdfsColumn(index = 2, max = OtherConstants.FDFS_FILE_EXT_NAME_MAX_LEN)
    private String fileExtName;

    /**
     * 构造函数
     *
     * @param storeIndex  存储节点index
     * @param inputStream 文件流
     * @param fileExtName 文件扩展名
     * @param fileSize 发送文件长度
     * @param isAppenderFile 是否追加文件
     */
    public StorageUploadFileRequest(byte storeIndex, InputStream inputStream, String fileExtName, long fileSize,
                                    boolean isAppenderFile) {
        super();
        this.inputFile = inputStream;
        this.fileSize = fileSize;
        this.storeIndex = storeIndex;
        this.fileExtName = fileExtName;
        if (isAppenderFile) {
            head = new ProtoHead(UPLOAD_APPENDER_CMD);
        } else {
            head = new ProtoHead(UPLOAD_CMD);
        }
    }

}
