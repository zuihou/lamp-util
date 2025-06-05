package com.xxl.job.core.context;

import lombok.Getter;
import lombok.Setter;

/**
 * xxl-job context
 *
 * @author xuxueli 2020-05-21
 * [Dear hj]
 */
@Getter
public class XxlJobContext {

    public static final int HANDLE_CODE_SUCCESS = 200;
    public static final int HANDLE_CODE_FAIL = 500;
    public static final int HANDLE_CODE_TIMEOUT = 502;

    // ---------------------- base info ----------------------
    private static final InheritableThreadLocal<XxlJobContext> CONTEXT_HOLDER = new InheritableThreadLocal<>(); // support for child thread of job handler)
    /**
     * job id
     */
    private final long jobId;

    // ---------------------- for log ----------------------
    /**
     * job param
     */
    private final String jobParam;

    // ---------------------- for shard ----------------------
    /**
     * job log filename
     */
    private final String jobLogFileName;
    /**
     * shard index
     */
    private final int shardIndex;

    // ---------------------- for handle ----------------------
    /**
     * shard total
     */
    private final int shardTotal;
    /**
     * handleCode：The result status of job execution
     *
     *      200 : success
     *      500 : fail
     *      502 : timeout
     *
     */
    @Setter
    private int handleCode;
    /**
     * handleMsg：The simple log msg of job execution
     */
    @Setter
    private String handleMsg;

    public XxlJobContext(long jobId, String jobParam, String jobLogFileName, int shardIndex, int shardTotal) {
        this.jobId = jobId;
        this.jobParam = jobParam;
        this.jobLogFileName = jobLogFileName;
        this.shardIndex = shardIndex;
        this.shardTotal = shardTotal;

        // default success
        this.handleCode = HANDLE_CODE_SUCCESS;
    }

    public static XxlJobContext getXxlJobContext() {
        return CONTEXT_HOLDER.get();
    }

    public static void setXxlJobContext(XxlJobContext xxlJobContext) {
        CONTEXT_HOLDER.set(xxlJobContext);
    }

    // ---------------------- tool ----------------------

}