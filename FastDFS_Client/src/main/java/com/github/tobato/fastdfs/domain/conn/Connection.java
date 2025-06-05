package com.github.tobato.fastdfs.domain.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 表示一个客户端与服务端的连接
 * <p>
 * 负责连接的管理
 *
 * @author tobato
 */
public interface Connection {

    /**
     * 关闭连接
     */
    void close();

    /**
     * 连接是否关闭
     *
     * @return 是否关闭
     */
    boolean isClosed();

    /**
     * 测试连接是否有效
     *
     * @return 是否有效
     */
    boolean isValid();

    /**
     * 获取输出流
     *
     * @return 输出流
     * @throws IOException 获取输出流错误
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * 获取输入流
     *
     * @return 输入流
     * @throws IOException 获取输入流错误
     */
    InputStream getInputStream() throws IOException;

    /**
     * 获取字符集
     *
     * @return 字符集
     */
    Charset getCharset();

}
