package com.github.zuihou.injection.core;

import com.github.zuihou.injection.annonation.InjectionField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zuihou
 * @date 2020/5/8 下午9:19
 */
@Data
@AllArgsConstructor
public class FieldParam {
    private InjectionField anno;
    private Serializable queryKey;
    private Object curField;
}
