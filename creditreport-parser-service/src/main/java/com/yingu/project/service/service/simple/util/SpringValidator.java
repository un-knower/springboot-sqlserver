package com.yingu.project.service.service.simple.util;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.SneakyThrows;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基于javax.validation实现bean验证
 *
 * @Date: Created in 2018/4/13 17:43
 * @Author: wm
 */
@Component
public class SpringValidator {

    @Autowired
    private Validator validator;

    /**
     * 验证对象及所有子对象
     * 递归实现方式，支持List、Set
     *
     * @param object
     * @param groups
     * @return
     */
    @SneakyThrows
    public <T> Set<ConstraintViolation> validate(T object, Class<?>... groups) {
        if (object==null){
            return null;
        }
        //循环object的所有属性
        Class clazz = object.getClass();
        //非自定义Object则跳过
        if (clazz.getName().startsWith("java.lang")) {
            return null;
        }
        Set<ConstraintViolation> result = new HashSet<>();
        if (object instanceof List) {
            List list = (List) object;
            for (Object item : list) {
                Set<ConstraintViolation> set = this.validate(item, groups);
                if (set != null && set.size() > 0) {
                    result.addAll(set);
                }
            }
        } else {
            Set<ConstraintViolation<T>> set = validator.validate(object, groups);
            if (set != null && set.size() > 0) {
                result.addAll(set);
            }
            //遍历所有属性，若存在验证注解则执行验证
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                //是否存在javax验证注解
                boolean hasValidAnnotation = false;
                for (Annotation annotation : field.getAnnotations()) {
                    String annoTypeName = annotation.annotationType().getName();
                    if (annoTypeName.startsWith("javax.validation")
                            || annoTypeName.startsWith("org.hibernate.validator")) {
                        hasValidAnnotation = true;
                        break;
                    }
                }
                //若存在javax验证注解，则再次调用validate，验证子属性
                if (hasValidAnnotation) {
//                if (true) {
                    if (field.get(object)==null){
                        continue;
                    }
                    T value = (T) field.get(object);
                    Set<ConstraintViolation> subSet = this.validate(value, groups);
                    if (subSet != null && subSet.size() > 0) {
                        result.addAll(subSet);
                    }
                }

                //若属性是List类型，则循环元素进行验证
                if ("java.util.List".equals(field.getType().getName())) {
                    if (field.get(object)==null){
                        continue;
                    }
                    T value = (T) field.get(object);
                    List list = (List) value;
                    for (Object item : list) {
                        Set<ConstraintViolation> subSet = this.validate(item, groups);
                        if (subSet != null && subSet.size() > 0) {
                            result.addAll(subSet);
                        }
                    }
                } else if ("java.util.Set".equals(field.getType().getName())) {
                    if (field.get(object)==null){
                        continue;
                    }
                    T value = (T) field.get(object);
                    Set setValue = (Set) value;
                    for (Object item : setValue) {
                        Set<ConstraintViolation> subSet = this.validate(item, groups);
                        if (subSet != null && subSet.size() > 0) {
                            result.addAll(subSet);
                        }
                    }
                }
            }
        }
        return result;
    }
}
