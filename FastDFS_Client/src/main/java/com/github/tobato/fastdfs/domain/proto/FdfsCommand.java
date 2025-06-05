package com.github.tobato.fastdfs.domain.proto;

import com.github.tobato.fastdfs.domain.conn.Connection;

/**
 * Fdfs交易命令抽象
 *
 * @param <T> 泛型
 * @author tobato
 */
public interface FdfsCommand<T> {

    /**
     * 执行交易
     */
    T execute(Connection conn);

}
