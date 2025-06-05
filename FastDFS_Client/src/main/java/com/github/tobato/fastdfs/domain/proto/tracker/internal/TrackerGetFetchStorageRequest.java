package com.github.tobato.fastdfs.domain.proto.tracker.internal;

import com.github.tobato.fastdfs.domain.proto.CmdConstants;
import com.github.tobato.fastdfs.domain.proto.FdfsRequest;
import com.github.tobato.fastdfs.domain.proto.OtherConstants;
import com.github.tobato.fastdfs.domain.proto.ProtoHead;
import com.github.tobato.fastdfs.domain.proto.mapper.DynamicFieldType;
import com.github.tobato.fastdfs.domain.proto.mapper.FdfsColumn;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

/**
 * 获取源服务器
 *
 * @author tobato
 */
@Getter
public class TrackerGetFetchStorageRequest extends FdfsRequest {

    private static final byte FETCH_CMD = CmdConstants.TRACKER_PROTO_CMD_SERVICE_QUERY_FETCH_ONE;
    private static final byte UPDATE_CMD = CmdConstants.TRACKER_PROTO_CMD_SERVICE_QUERY_UPDATE;

    /**
     * 组名
     */
    @FdfsColumn(index = 0, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private final String groupName;
    /**
     * 路径名
     */
    @FdfsColumn(index = 1, dynamicField = DynamicFieldType.allRestByte)
    private final String path;

    /**
     * 获取文件源服务器
     *
     * @param groupName 分组
     * @param path 文件路径
     */
    public TrackerGetFetchStorageRequest(String groupName, String path, boolean toUpdate) {
        Validate.notBlank(groupName, "分组不能为空");
        Validate.notBlank(path, "文件路径不能为空");
        this.groupName = groupName;
        this.path = path;
        if (toUpdate) {
            head = new ProtoHead(UPDATE_CMD);
        } else {
            head = new ProtoHead(FETCH_CMD);
        }
    }

}
