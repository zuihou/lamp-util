package top.tangyh.basic.database.plugins;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Table;

/**
 * COLUMN 级别多租户拦截器
 * 相对于mybatis-plus 的 TenantLineInnerInterceptor，本插件支持where条件拼接多个租户id
 *
 * @author zuihou
 * @author hubin
 * @version v1.0
 * @date 2021/5/17 8:49 下午
 * @create [2021/5/17 8:49 下午 ] [hubin] [初始创建]
 * @create [2021/5/17 8:49 下午 ] [zuihou] [增强]
 * @see TenantLineInnerInterceptor
 */
public class MultiTenantLineInnerInterceptor extends TenantLineInnerInterceptor {
    /**
     * 处理条件
     */
    @Override
    protected Expression builderExpression(Expression currentExpression, Table table) {
        // @author zuihou 增强：有list优先使用，否则执行 mybatis-plus 原来的逻辑
        TenantLineHandler tenantLineHandler = getTenantLineHandler();
        if (tenantLineHandler instanceof MultiTenantLineHandler) {
            ValueListExpression listExpression = ((MultiTenantLineHandler) tenantLineHandler).getTenantIdList();
            if (listExpression != null) {
                InExpression in = new InExpression();
                in.setLeftExpression(this.getAliasColumn(table));
                in.setRightExpression(listExpression);
                if (currentExpression == null) {
                    return in;
                }
                if (currentExpression instanceof OrExpression) {
                    return new AndExpression(new Parenthesis(currentExpression), in);
                } else {
                    return new AndExpression(currentExpression, in);
                }
            }
        }

        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(this.getAliasColumn(table));
        equalsTo.setRightExpression(getTenantLineHandler().getTenantId());
        if (currentExpression == null) {
            return equalsTo;
        }
        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), equalsTo);
        } else {
            return new AndExpression(currentExpression, equalsTo);
        }
    }
}
