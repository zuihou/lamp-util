package top.tangyh.basic.log.event;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import top.tangyh.basic.model.log.OptLogDTO;

import java.util.function.Consumer;


/**
 * 异步监听日志事件
 *
 * @author zuihou
 * @date 2019-07-01 15:13
 */
@Slf4j
@AllArgsConstructor
public class SysLogListener {

    private final Consumer<OptLogDTO> consumer;

    @Async
    @Order
    @EventListener(SysLogEvent.class)
    public void saveSysLog(SysLogEvent event) {
        OptLogDTO sysLog = (OptLogDTO) event.getSource();
        consumer.accept(sysLog);
    }

}
