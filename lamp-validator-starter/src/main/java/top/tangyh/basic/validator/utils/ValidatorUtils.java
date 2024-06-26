package top.tangyh.basic.validator.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.DefaultPropertyNodeNameProvider;
import org.hibernate.validator.internal.properties.DefaultGetterPropertySelectionStrategy;
import org.hibernate.validator.internal.properties.javabean.JavaBeanHelper;
import org.hibernate.validator.spi.nodenameprovider.PropertyNodeNameProvider;
import org.hibernate.validator.spi.properties.GetterPropertySelectionStrategy;
import top.tangyh.basic.annotation.constraints.NotEmptyPattern;
import top.tangyh.basic.validator.constraintvalidators.LengthConstraintValidator;
import top.tangyh.basic.validator.constraintvalidators.NotEmptyConstraintValidator;
import top.tangyh.basic.validator.constraintvalidators.NotEmptyPatternConstraintValidator;
import top.tangyh.basic.validator.constraintvalidators.NotNullConstraintValidator;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * HibernateValidate 校验工具类
 *
 * @since 2024年06月24日14:29:43
 * @author tangyh
 */
public class ValidatorUtils {

    private final static Validator VALIDATOR_FAST = warp(Validation.byProvider(HibernateValidator.class).configure().failFast(true)).buildValidatorFactory().getValidator();
    private final static Validator VALIDATOR_ALL = warp(Validation.byProvider(HibernateValidator.class).configure().failFast(false)).buildValidatorFactory().getValidator();

    /**
     * 校验遇到第一个不合法的字段直接返回不合法字段，后续字段不再校验
     *
     * @since 2024年06月24日11:31:14
     * @param <T> 实体泛型
     * @param domain 实体
     * @return
     */
    public static <T> Set<ConstraintViolation<T>> validateFast(T domain) {
        Set<ConstraintViolation<T>> validateResult = VALIDATOR_FAST.validate(domain);
        if (!validateResult.isEmpty()) {
            System.out.println(validateResult.iterator().next().getPropertyPath() + "：" + validateResult.iterator().next().getMessage());
        }
        return validateResult;
    }

    /**
     * 校验所有字段并返回不合法字段
     * @since 2024年06月24日11:31:36
     * @param <T>
     * @param domain
     * @return
     * @throws Exception
     */
    public static <T> Set<ConstraintViolation<T>> validateAll(T domain) {
        Set<ConstraintViolation<T>> validateResult = VALIDATOR_ALL.validate(domain);
        if (!validateResult.isEmpty()) {
            Iterator<ConstraintViolation<T>> it = validateResult.iterator();
            while (it.hasNext()) {
                ConstraintViolation<T> cv = it.next();

                Field field = ReflectUtil.getField(cv.getRootBeanClass(), cv.getPropertyPath().toString());
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                String name = "";
                if (excelProperty == null) {
                    Schema apiModelProperty = field.getAnnotation(Schema.class);
                    name = apiModelProperty != null ? apiModelProperty.description() : "";
                } else {
                    name = StrUtil.join(".", excelProperty.value());
                }

                System.out.println(name + ": " + cv.getPropertyPath() + "：" + cv.getMessage());

            }
        }
        return validateResult;
    }


    public static <T> String validateAll(List<T> domains, int headRow) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < domains.size(); i++) {
            T domain = domains.get(i);
            Set<ConstraintViolation<T>> validateResult = VALIDATOR_ALL.validate(domain);
            if (!validateResult.isEmpty()) {
                Iterator<ConstraintViolation<T>> it = validateResult.iterator();
                while (it.hasNext()) {
                    ConstraintViolation<T> cv = it.next();

                    Field field = ReflectUtil.getField(cv.getRootBeanClass(), cv.getPropertyPath().toString());
                    ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    String name = "";
                    if (excelProperty != null) {
                        name = StrUtil.join(".", excelProperty.value());
                    }
                    if (StrUtil.isEmpty(name)) {
                        Schema apiModelProperty = field.getAnnotation(Schema.class);
                        name = apiModelProperty != null ? apiModelProperty.description() : "";
                    }
                    if (StrUtil.isEmpty(name)) {
                        name = field.getName();
                    }

                    Object value = BeanUtil.getFieldValue(domain, field.getName());
                    sb.append(StrUtil.format("第{}行，{} = {} : {}<br/>", (headRow + i + 1), name, value, cv.getMessage()));

                }
            }
        }

        return sb.toString();
    }


    public static Configuration<HibernateValidatorConfiguration> warp(HibernateValidatorConfiguration configuration) {
        addValidatorMapping(configuration);
        //其他操作
        return configuration;
    }

    private static void addValidatorMapping(HibernateValidatorConfiguration configuration) {
        // 增加一个我们自定义的校验处理器与length的映射
        GetterPropertySelectionStrategy getterPropertySelectionStrategyToUse = new DefaultGetterPropertySelectionStrategy();
        PropertyNodeNameProvider defaultPropertyNodeNameProvider = new DefaultPropertyNodeNameProvider();
        ConstraintMapping mapping = new DefaultConstraintMapping(new JavaBeanHelper(getterPropertySelectionStrategyToUse, defaultPropertyNodeNameProvider));

        ConstraintDefinitionContext<Length> length = mapping.constraintDefinition(Length.class);
        length.includeExistingValidators(true);
        length.validatedBy(LengthConstraintValidator.class);

        ConstraintDefinitionContext<NotNull> notNull = mapping.constraintDefinition(NotNull.class);
        notNull.includeExistingValidators(true);
        notNull.validatedBy(NotNullConstraintValidator.class);

        ConstraintDefinitionContext<NotEmpty> notEmpty = mapping.constraintDefinition(NotEmpty.class);
        notEmpty.includeExistingValidators(true);
        notEmpty.validatedBy(NotEmptyConstraintValidator.class);

        ConstraintDefinitionContext<NotEmptyPattern> notEmptyPattern = mapping.constraintDefinition(NotEmptyPattern.class);
        notEmptyPattern.includeExistingValidators(true);
        notEmptyPattern.validatedBy(NotEmptyPatternConstraintValidator.class);

        configuration.addMapping(mapping);
    }

}
