package com.github.zuihou.log.util;

import com.github.zuihou.log.annotation.SysLog;
import com.github.zuihou.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 日志工具类
 *
 * @author zuihou
 * @date 2019-04-28 11:30
 */
@Slf4j
public class LogUtil {

    /***
     * 获取操作信息
     * @param point
     * @return
     */
    public static String getDescribe(JoinPoint point) {
        SysLog annotation = getTargetAnno(point);
        if (annotation == null) {
            return StrPool.EMPTY;
        }
        return annotation.value();
    }

    public static String getDescribe(SysLog annotation) {
        if (annotation == null) {
            return StrPool.EMPTY;
        }
        return annotation.value();
    }

    /**
     * 优先从子类获取 @SysLog：
     * 1，若子类重写了该方法，有标记就记录日志，没标记就忽略日志
     * 2，若子类没有重写该方法，就从父类获取，父类有标记就记录日志，没标记就忽略日志
     *
     * @param point
     * @return
     */
    public static SysLog getTargetAnno(JoinPoint point) {
        try {
//            // 获取连接点签名的方法名
//            String methodName = point.getSignature().getName();
//            //获取连接点参数
//            Object[] args = point.getArgs();
//            if (ArrayUtil.hasNull(args)) { // 参数为空时，只能参数长度
//
//            } else {  // 参数不为空时，能准确反射到具体的方法
//                Method method = ReflectUtil.getMethodOfObj(point.getTarget(), methodName, args);
////                Class[] classes = Arrays.stream(args).filter(Objects::isNull).map((arg) -> arg.getClass()).toArray(Class[]::new);
//
//                if (method != null) {
//                    annotation = method.getAnnotation(SysLog.class);
//                } else {
//                    Method parentMethod = ReflectUtil.getMethodOfObj(point.getTarget().getClass().getSuperclass(), methodName, args);
//                    if (parentMethod != null) {
//                        annotation = parentMethod.getAnnotation(SysLog.class);
//                    }
//                }
//            }

            SysLog annotation = null;
            if (point.getSignature() instanceof MethodSignature) {
                Method method = ((MethodSignature) point.getSignature()).getMethod();
                if (method != null) {
                    annotation = method.getAnnotation(SysLog.class);
                }
            }
            return annotation;
        } catch (Exception e) {
            log.warn("获取 {}.{} 的 @SysLog 注解失败", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), e);
            return null;
        }
    }

}
