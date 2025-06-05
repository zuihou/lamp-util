package com.github.tobato.fastdfs.domain.proto.tracker.internal;

import com.github.tobato.fastdfs.domain.proto.CmdConstants;
import com.github.tobato.fastdfs.domain.proto.FdfsRequest;
import com.github.tobato.fastdfs.domain.proto.OtherConstants;
import com.github.tobato.fastdfs.domain.proto.ProtoHead;
import com.github.tobato.fastdfs.domain.proto.mapper.FdfsColumn;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

/**
 * 按分组获取存储节点
 *
 * @author tobato
 */
@Getter
public class TrackerGetStoreStorageWithGroupRequest extends FdfsRequest {

    private static final byte WITH_GROUP_CMD = CmdConstants.TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITH_GROUP_ONE;

    /**
     * 分组定义
     */
    @FdfsColumn(index = 0, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private final String groupName;

    /**
     * 获取存储节点
     *
     * @param groupName 分组
     */
    public TrackerGetStoreStorageWithGroupRequest(String groupName) {
        Validate.notBlank(groupName, "分组不能为空");
        this.groupName = groupName;
        this.head = new ProtoHead(WITH_GROUP_CMD);
    }

}
