package top.tangyh.basic.base.controller;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.tangyh.basic.annotation.log.SysLog;
import top.tangyh.basic.annotation.security.PreAuth;
import top.tangyh.basic.base.R;
import top.tangyh.basic.base.entity.SuperEntity;

/**
 * 修改Controller
 *
 * @param <Entity>    实体
 * @param <UpdateDTO> 修改参数
 * @author zuihou
 * @date 2020年03月07日22:30:37
 */
public interface UpdateController<Entity, UpdateDTO> extends BaseController<Entity> {

    /**
     * 修改
     *
     * @param updateDTO 修改DTO
     * @return 修改后的实体数据
     */
    @Operation(summary = "修改", description = "修改UpdateDTO中不为空的字段")
    @PutMapping
    @SysLog(value = "'修改:' + #updateDTO?.id", request = false)
    @PreAuth("hasAnyPermission('{}edit')")
    default R<Entity> update(@RequestBody @Validated(SuperEntity.Update.class) UpdateDTO updateDTO) {
        R<Entity> result = handlerUpdate(updateDTO);
        if (result.getDefExec()) {
            Entity model = BeanUtil.toBean(updateDTO, getEntityClass());
            getBaseService().updateById(model);
            result.setData(model);
        }
        return result;
    }

    /**
     * 修改所有字段
     *
     * @param entity 实体
     * @return
     */
    @Operation(summary = "修改所有字段", description = "修改所有字段，没有传递的字段会被置空")
    @PutMapping("/all")
    @SysLog(value = "'修改所有字段:' + #entity?.id", request = false)
    @PreAuth("hasAnyPermission('{}edit')")
    default R<Entity> updateAll(@RequestBody @Validated(SuperEntity.Update.class) Entity entity) {
        getBaseService().updateAllById(entity);
        return R.success(entity);
    }

    /**
     * 自定义更新
     *
     * @param model 修改DTO
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    default R<Entity> handlerUpdate(UpdateDTO model) {
        return R.successDef();
    }
}
