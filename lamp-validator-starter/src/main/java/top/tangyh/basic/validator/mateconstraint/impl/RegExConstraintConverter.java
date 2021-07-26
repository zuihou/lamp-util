package top.tangyh.basic.validator.mateconstraint.impl;


import top.tangyh.basic.annotation.constraints.NotEmptyPattern;
import top.tangyh.basic.validator.mateconstraint.IConstraintConverter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * 正则校验规则
 *
 * @author zuihou
 */
public class RegExConstraintConverter extends BaseConstraintConverter implements IConstraintConverter {
    @Override
    protected String getType(Class<? extends Annotation> type) {
        return "RegEx";
    }

    @Override
    protected List<Class<? extends Annotation>> getSupport() {
        return Arrays.asList(Pattern.class, Email.class, URL.class, NotEmptyPattern.class);
    }

    @Override
    protected List<String> getMethods() {
        return Arrays.asList("regexp", "message");
    }
}
