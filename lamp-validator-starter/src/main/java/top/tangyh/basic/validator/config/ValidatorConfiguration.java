package top.tangyh.basic.validator.config;

import jakarta.validation.Configuration;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.tangyh.basic.annotation.constraints.NotEmptyPattern;
import top.tangyh.basic.validator.constraintvalidators.LengthConstraintValidator;
import top.tangyh.basic.validator.constraintvalidators.NotEmptyConstraintValidator;
import top.tangyh.basic.validator.constraintvalidators.NotEmptyPatternConstraintValidator;
import top.tangyh.basic.validator.constraintvalidators.NotNullConstraintValidator;
import top.tangyh.basic.validator.controller.FormValidatorController;
import top.tangyh.basic.validator.extract.DefaultConstraintExtractImpl;
import top.tangyh.basic.validator.extract.IConstraintExtract;

/**
 * 验证器配置
 *
 * @author zuihou
 * @date 2019/07/14
 */
public class ValidatorConfiguration {

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = warp(Validation.byProvider(HibernateValidator.class)
                .configure()
                //快速失败返回模式
                .addProperty("hibernate.validator.fail_fast", "true"))
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        return validator;
    }

    private Configuration<HibernateValidatorConfiguration> warp(HibernateValidatorConfiguration configuration) {
        addValidatorMapping(configuration);
        //其他操作
        return configuration;
    }

    private void addValidatorMapping(HibernateValidatorConfiguration configuration) {
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

    /**
     * Method:  开启快速返回
     * Description:
     * 如果参数校验有异常，直接抛异常，不会进入到 controller，使用全局异常拦截进行拦截
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator);
        return postProcessor;
    }

    @Bean
    public IConstraintExtract constraintExtract(Validator validator) {
        return new DefaultConstraintExtractImpl(validator);
    }

//    @Bean
//    public FormValidatorController getFormValidatorController(IConstraintExtract constraintExtract, RequestMappingHandlerMapping requestMappingHandlerMapping) {
//        return new FormValidatorController(constraintExtract, requestMappingHandlerMapping);
//    }

}
