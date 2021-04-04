package com.newbiegroup.hermes.common.config.provider;

import com.newbiegroup.hermes.common.config.RpcConfigAbstract;

/**
 * <p>ClassName: 接口名称 程序对象  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/4 10:46
 */
public class ProviderConfig extends RpcConfigAbstract {

    //服务具体实现类
    protected Object ref;

    //socketAddress host+port;
    protected String address;

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
