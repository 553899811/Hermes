package com.newbiegroup.rpc.remoting.codec;

import java.io.Serializable;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/3 17:01
 */
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 5079946890572242095L;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 请求类名称
     */
    private String className;

    /**
     * 请求方法名称
     */
    private String methodName;

    /**
     * 入参类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 入参变量
     */
    private Object[] parameters;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
