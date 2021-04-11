package com.newbiegroup.hermes.example.hermesJunitTest.provider;

import com.newbiegroup.hermes.common.config.provider.ProviderConfig;
import com.newbiegroup.hermes.rpc.config.RpcServerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/12 0:54
 */
public class ProviderStarter {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProviderConfig providerConfig = new ProviderConfig();
                    providerConfig.setInterface("com.newbiegroup.hermes.example.hermesJunitTest.consumer.HelloService");
                    HelloServiceImpl helloServiceImpl = HelloServiceImpl.class.newInstance();
                    providerConfig.setRef(helloServiceImpl);

                    List<ProviderConfig> providerConfigs = new ArrayList<>();
                    providerConfigs.add(providerConfig);

                    RpcServerConfig rpcServerConfig = new RpcServerConfig(providerConfigs);
                    rpcServerConfig.setPort(8765);
                    rpcServerConfig.exporter();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
