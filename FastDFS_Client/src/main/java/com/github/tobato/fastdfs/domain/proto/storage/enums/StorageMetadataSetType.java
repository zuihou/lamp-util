package com.github.tobato.fastdfs.domain.proto.storage.enums;

import lombok.Getter;

/**
 * 元数据设置方式
 *
 * @author tobato
 */
@Getter
public enum StorageMetadataSetType {

    /**
     * 覆盖
     */
    STORAGE_SET_METADATA_FLAG_OVERWRITE((byte) 'O'),
    /**
     * 没有的条目增加，有则条目覆盖
     */
    STORAGE_SET_METADATA_FLAG_MERGE((byte) 'M');

    private final byte type;

    StorageMetadataSetType(byte type) {
        this.type = type;
    }

}
