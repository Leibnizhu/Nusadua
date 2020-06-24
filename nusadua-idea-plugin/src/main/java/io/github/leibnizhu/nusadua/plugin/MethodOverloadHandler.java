package io.github.leibnizhu.nusadua.plugin;

import com.intellij.openapi.components.ServiceManager;

/**
 * @author Leibniz on 2020/06/24 11:45 PM
 */
public interface MethodOverloadHandler {
    static MethodOverloadHandler getInstance() {
        return ServiceManager.getService(MethodOverloadHandler.class);
    }
}
