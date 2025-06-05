package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.IdleBeatParam;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.LogParam;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {

    /**
     * beat
     * @return beat
     */
    ReturnT<String> beat();

    /**
     * idle beat
     *
     * @param idleBeatParam 参数
     * @return idle beat
     */
    ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

    /**
     * run
     * @param triggerParam 参数
     * @return run
     */
    ReturnT<String> run(TriggerParam triggerParam);

    /**
     * kill
     * @param killParam 参数
     * @return kill
     */
    ReturnT<String> kill(KillParam killParam);

    /**
     * log
     * @param logParam 参数
     * @return log
     */
    ReturnT<LogResult> log(LogParam logParam);

}
