package com.newbiegroup.hermes.common.config;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/4 9:35
 */
public abstract class RpcConfigAbstract {
    private AtomicInteger generator = new AtomicInteger(0);

    protected String id;

    private String interfaceClass = null;

    // 服务的调用方(consumer端特有的属性)
    protected Class<?> proxyClass = null;


    public String getId() {
        if (StringUtils.isBlank(id)) {
            id = "hermes-cfg-gen-" + generator.getAndIncrement();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterface() {
        return interfaceClass;
    }

    public void setInterface(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}
