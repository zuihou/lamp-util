package top.tangyh.basic.validator.extract;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import top.tangyh.basic.utils.StrPool;
import top.tangyh.basic.validator.mateconstraint.IConstraintConverter;
import top.tangyh.basic.validator.mateconstraint.impl.*;
import top.tangyh.basic.validator.model.ConstraintInfo;
import top.tangyh.basic.validator.model.FieldValidatorDesc;
import top.tangyh.basic.validator.model.ValidConstraint;

import javax.validation.Validator;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static top.tangyh.basic.utils.StrPool.*;
import static top.tangyh.basic.validator.utils.ValidatorConstants.*;

/**
 * 缺省的约束提取器
 *
 * @author zuihou
 * @date 2019-07-14 12:12
 */
@Slf4j
public class DefaultConstraintExtractImpl implements IConstraintExtract {

    private final Map<String, Map<String, FieldValidatorDesc>> CACHE = new HashMap<>();

    private final Validator validator;
    private BeanMetaDataManager beanMetaDataManager;
    private List<IConstraintConverter> constraintConverters;

    public DefaultConstraintExtractImpl(final Validator validator) {
        this.validator = validator;
        init();
    }

    public final void init() {
        try {
            Field beanMetaDataManagerField = ValidatorImpl.class.getDeclaredField("beanMetaDataManager");
            beanMetaDataManagerField.setAccessible(true);
            beanMetaDataManager = (BeanMetaDataManager) beanMetaDataManagerField.get(validator);
            constraintConverters = new ArrayList<>(10);
            constraintConverters.add(new MaxMinConstraintConverter());
            constraintConverters.add(new NotNullConstraintConverter());
            constraintConverters.add(new RangeConstraintConverter());
            constraintConverters.add(new RegExConstraintConverter());
            constraintConverters.add(new OtherConstraintConverter());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("初始化验证器失败", e);
        }
    }

    @Override
    public Collection<FieldValidatorDesc> extract(List<ValidConstraint> constraints) throws Exception {
        if (constraints == null || constraints.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, FieldValidatorDesc> fieldValidatorDesc = new HashMap((int) (constraints.size() / 0.75 + 1));
        for (ValidConstraint constraint : constraints) {
            doExtract(constraint, fieldValidatorDesc);
        }

        return fieldValidatorDesc.values();
    }


    private void doExtract(ValidConstraint constraint, Map<String, FieldValidatorDesc> fieldValidatorDesc) throws Exception {
        Class<?> targetClazz = constraint.getTarget();
        Class<?>[] groups = constraint.getGroups();

        String key = targetClazz.getName() + StrPool.COLON +
                Arrays.stream(groups).map(Class::getName).collect(Collectors.joining(StrPool.COLON));
        if (CACHE.containsKey(key)) {
            fieldValidatorDesc.putAll(CACHE.get(key));
            return;
        }

        //测试一下这个方法
        //validator.getConstraintsForClass(targetClazz).getConstrainedProperties()

        BeanMetaData<?> res = beanMetaDataManager.getBeanMetaData(targetClazz);
        Set<MetaConstraint<?>> r = res.getMetaConstraints();
        Set<PropertyDescriptor> constrainedProperties = res.getBeanDescriptor().getConstrainedProperties();
        for (MetaConstraint<?> metaConstraint : r) {
            builderFieldValidatorDesc(metaConstraint, constrainedProperties, groups, fieldValidatorDesc);
        }

        CACHE.put(key, fieldValidatorDesc);
    }


    private void builderFieldValidatorDesc(MetaConstraint<?> metaConstraint,
                                           Set<PropertyDescriptor> constraintDescriptors,
                                           Class<?>[] groups,
                                           Map<String, FieldValidatorDesc> fieldValidatorDesc) throws Exception {
        //字段上的组
        Set<Class<?>> groupsMeta = metaConstraint.getGroupList();
        boolean isContainsGroup = false;

        //需要验证的组
        for (Class<?> group : groups) {
            if (groupsMeta.contains(group)) {
                isContainsGroup = true;
                break;
            }
            for (Class<?> g : groupsMeta) {
                if (g.isAssignableFrom(group)) {
                    isContainsGroup = true;
                    break;
                }
            }
        }
        if (!isContainsGroup) {
            return;
        }

        ConstraintLocation con = metaConstraint.getLocation();
        String domainName = con.getDeclaringClass().getSimpleName();
        String fieldName = con.getConstrainable().getName();
        String key = domainName + fieldName;

        boolean flag = false;
        for (PropertyDescriptor constraintDescriptor : constraintDescriptors) {
            if (constraintDescriptor.getPropertyName().equals(fieldName)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            return;
        }
        FieldValidatorDesc desc = fieldValidatorDesc.get(key);
        if (desc == null) {
            desc = new FieldValidatorDesc();
            desc.setField(fieldName);
            desc.setFieldType(getType(con.getConstrainable().getType().getTypeName()));
            desc.setConstraints(new ArrayList<>());
            fieldValidatorDesc.put(key, desc);
        }
        ConstraintInfo constraint = builderConstraint(metaConstraint.getDescriptor().getAnnotation());
        desc.getConstraints().add(constraint);

        if (PATTERN.equals(metaConstraint.getDescriptor().getAnnotationType().getSimpleName())) {
            ConstraintInfo notNull = new ConstraintInfo();
            notNull.setType(NOT_NULL);
            Map<String, Object> attrs = MapUtil.newHashMap();
            attrs.put(MESSAGE, "不能为空");
            notNull.setAttrs(attrs);
            desc.getConstraints().add(notNull);
        }
    }


    private String getType(String typeName) {
        if (StrUtil.startWithAny(typeName, SET_TYPE_NAME, LIST_TYPE_NAME, COLLECTION_TYPE_NAME)) {
            return ARRAY;
        } else if (StrUtil.equalsAny(typeName, LONG_TYPE_NAME, INTEGER_TYPE_NAME, SHORT_TYPE_NAME)) {
            return INTEGER;
        } else if (StrUtil.equalsAny(typeName, DOUBLE_TYPE_NAME, FLOAT_TYPE_NAME)) {
            return FLOAT;
        } else if (StrUtil.equalsAny(typeName, LOCAL_DATE_TIME_TYPE_NAME, DATE_TYPE_NAME)) {
            return DATETIME;
        } else if (StrUtil.equalsAny(typeName, LOCAL_DATE_TYPE_NAME)) {
            return DATE;
        } else if (StrUtil.equalsAny(typeName, LOCAL_TIME_TYPE_NAME)) {
            return TIME;
        } else if (StrUtil.equalsAny(typeName, BOOLEAN_TYPE_NAME)) {
            return BOOLEAN;
        }
        return StrUtil.subAfter(typeName, CharUtil.DOT, true);
    }

    private ConstraintInfo builderConstraint(Annotation annotation) throws Exception {
        for (IConstraintConverter constraintConverter : constraintConverters) {
            if (constraintConverter.support(annotation.annotationType())) {
                return constraintConverter.converter(annotation);
            }
        }
        return null;
    }
}
