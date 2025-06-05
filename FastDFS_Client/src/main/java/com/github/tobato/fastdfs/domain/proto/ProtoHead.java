package com.github.tobato.fastdfs.domain.proto;

import com.github.tobato.fastdfs.domain.proto.mapper.BytesUtil;
import com.github.tobato.fastdfs.exception.FdfsServerException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * FDFS协议头定义
 * <p>
 * <pre>
 * FDFS协议头一共10位
 * </pre>
 *
 * @author tobato
 */
@Getter
public class ProtoHead {

    /**
     * 报文长度
     */
    private static final int HEAD_LENGTH = OtherConstants.FDFS_PROTO_PKG_LEN_SIZE + 2;

    /**
     * 报文内容长度 1-7位
     */
    @Setter
    private long contentLength = 0;
    /**
     * 报文类型 8位
     */
    private final byte cmd;
    /**
     * 处理状态 9位
     */
    private byte status = (byte) 0;

    /**
     * 请求报文构造函数
     */
    public ProtoHead(byte cmd) {
        super();
        this.cmd = cmd;
    }

    /**
     * 返回报文构造函数
     *
     * @param contentLength 报文内容长度
     * @param cmd 报文类型
     * @param status 处理状态
     */
    public ProtoHead(long contentLength, byte cmd, byte status) {
        super();
        this.contentLength = contentLength;
        this.cmd = cmd;
        this.status = status;
    }

    /**
     * 读取输入流创建报文头
     *
     * @param ins 文件流
     * @return FDFS协议头定义
     * @throws IOException 异常
     */
    public static ProtoHead createFromInputStream(InputStream ins) throws IOException {

        byte[] header = new byte[HEAD_LENGTH];
        int bytes = ins.read(header);
        // 读取HEAD_LENGTH长度的输入流
        if (bytes != header.length) {
            throw new IOException("recv package size " + bytes + " != " + header.length);
        }
        long returnContentLength = BytesUtil.buff2long(header, 0);
        byte returnCmd = header[OtherConstants.PROTO_HEADER_CMD_INDEX];
        byte returnStatus = header[OtherConstants.PROTO_HEADER_STATUS_INDEX];
        // 返回解析出来的ProtoHead
        return new ProtoHead(returnContentLength, returnCmd, returnStatus);

    }

    /**
     * toByte
     *
     * @return 字节
     */
    public byte[] toByte() {
        byte[] header;
        byte[] hexLen;

        header = new byte[HEAD_LENGTH];
        Arrays.fill(header, (byte) 0);
        hexLen = BytesUtil.long2buff(contentLength);
        System.arraycopy(hexLen, 0, header, 0, hexLen.length);
        header[OtherConstants.PROTO_HEADER_CMD_INDEX] = cmd;
        header[OtherConstants.PROTO_HEADER_STATUS_INDEX] = status;
        return header;
    }

    /**
     * 验证服务端返回报文有效性
     *
     * @return 有效性
     * @throws IOException 异常
     */
    public boolean validateResponseHead() throws IOException {
        // 检查是否是正确反馈报文
        if (cmd != CmdConstants.FDFS_PROTO_CMD_RESP) {
            throw new IOException(
                    "recv cmd: " + cmd + " is not correct, expect cmd: " + CmdConstants.FDFS_PROTO_CMD_RESP);
        }
        // 获取处理错误状态
        if (status != 0) {
            throw FdfsServerException.byCode(status);
        }

        if (contentLength < 0) {
            throw new IOException("recv body length: " + contentLength + " < 0!");
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProtoHead [contentLength=" + contentLength + ", cmd=" + cmd + ", status=" + status + "]";
    }

}
