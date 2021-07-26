package top.tangyh.basic.base.controller;

import top.tangyh.basic.annotation.log.SysLog;
import top.tangyh.basic.annotation.security.PreAuth;
import top.tangyh.basic.base.R;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;


/**
 * 删除Controller
 *
 * @param <Entity> 实体
 * @param <Id>     主键
 * @author zuihou
 * @date 2020年03月07日22:02:16
 */
public interface DeleteController<Entity, Id extends Serializable> extends BaseController<Entity> {

    /**
     * 删除方法
     *
     * @param ids id
     * @return 是否成功
     */
    @ApiOperation(value = "删除")
    @DeleteMapping
    @SysLog("'删除:' + #ids")
    @PreAuth("hasAnyPermission('{}delete')")
    default R<Boolean> delete(@RequestBody List<Id> ids) {
        R<Boolean> result = handlerDelete(ids);
        if (result.getDefExec()) {
            getBaseService().removeByIds(ids);
        }
        return result;
    }

    /**
     * 自定义删除
     *
     * @param ids id
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    default R<Boolean> handlerDelete(List<Id> ids) {
        return R.successDef(true);
    }

}
