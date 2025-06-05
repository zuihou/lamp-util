package com.xxl.job.core.context;

/**
 * xxl-job context
 *
 * @author xuxueli 2020-05-21
 * [Dear hj]
 */
public class XxlJobContext {

    public static final int HANDLE_CODE_SUCCESS = 200;
    public static final int HANDLE_CODE_FAIL = 500;
    public static final int HANDLE_CODE_TIMEOUT = 502;

    // ---------------------- base info ----------------------
    private static InheritableThreadLocal<XxlJobContext> contextHolder = new InheritableThreadLocal<XxlJobContext>(); // support for child thread of job handler)
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
    private int handleCode;
    /**
     * handleMsg：The simple log msg of job execution
     */
    private String handleMsg;

    public XxlJobContext(long jobId, String jobParam, String jobLogFileName, int shardIndex, int shardTotal) {
        this.jobId = jobId;
        this.jobParam = jobParam;
        this.jobLogFileName = jobLogFileName;
        this.shardIndex = shardIndex;
        this.shardTotal = shardTotal;

        this.handleCode = HANDLE_CODE_SUCCESS;  // default success
    }

    public static XxlJobContext getXxlJobContext() {
        return contextHolder.get();
    }

    public static void setXxlJobContext(XxlJobContext xxlJobContext) {
        contextHolder.set(xxlJobContext);
    }

    public long getJobId() {
        return jobId;
    }

    public String getJobParam() {
        return jobParam;
    }

    public String getJobLogFileName() {
        return jobLogFileName;
    }

    public int getShardIndex() {
        return shardIndex;
    }

    public int getShardTotal() {
        return shardTotal;
    }

    public int getHandleCode() {
        return handleCode;
    }

    // ---------------------- tool ----------------------

    public void setHandleCode(int handleCode) {
        this.handleCode = handleCode;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

}