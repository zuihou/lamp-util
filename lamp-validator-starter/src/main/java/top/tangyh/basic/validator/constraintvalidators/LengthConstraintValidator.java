package top.tangyh.basic.validator.constraintvalidators;


import top.tangyh.basic.interfaces.validator.IValidatable;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.constraintvalidators.hv.LengthValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义一个验证length的校验器。自定义类需要实现IValidatable接口
 *
 * @author zuihou
 * @date 2020年02月02日20:59:21
 */
public class LengthConstraintValidator implements ConstraintValidator<Length, IValidatable> {

    private final LengthValidator lengthValidator = new LengthValidator();

    @Override
    public void initialize(Length parameters) {
        lengthValidator.initialize(parameters);
    }

    @Override
    public boolean isValid(IValidatable value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.value() == null) {
            return true;
        }
        return lengthValidator.isValid(String.valueOf(value.value()), constraintValidatorContext);
    }
}
