package com.xxl.job.core.handler.impl;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;
import lombok.Getter;

/**
 * glue job handler
 *
 * @author xuxueli 2016-5-19 21:05:45
 */
public class GlueJobHandler extends IJobHandler {

    private final IJobHandler jobHandler;
    @Getter
    private long glueUpdateTime;

    public GlueJobHandler(IJobHandler jobHandler, long glueUpdateTime) {
        this.jobHandler = jobHandler;
        this.glueUpdateTime = glueUpdateTime;
    }

    @Override
    public void execute() throws Exception {
        XxlJobHelper.log("----------- glue.version:" + glueUpdateTime + " -----------");
        jobHandler.execute();
    }

    @Override
    public void init() throws Exception {
        this.jobHandler.init();
    }

    @Override
    public void destroy() throws Exception {
        this.jobHandler.destroy();
    }
}
