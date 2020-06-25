package io.github.leibnizhu.nusadua.plugin;

import com.intellij.openapi.components.ServiceManager;

/**
 * @author Leibniz on 2020/06/25 12:20 AM
 */
public class MethodOverloadHandler {
    public static MethodOverloadHandler getInstance() {
        return ServiceManager.getService(MethodOverloadHandler.class);
    }
}
