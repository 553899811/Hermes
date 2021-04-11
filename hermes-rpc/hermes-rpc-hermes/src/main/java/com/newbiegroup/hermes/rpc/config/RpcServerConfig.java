package com.newbiegroup.hermes.rpc.config;

import com.newbiegroup.hermes.rpc.RpcServer;
import com.newbiegroup.hermes.common.config.provider.ProviderConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/11 23:54
 */
@Slf4j
public class RpcServerConfig {

    private final String host = "127.0.0.1";

    protected int port;

    private List<ProviderConfig> providerConfigs;

    private RpcServer rpcServer = null;

    public RpcServerConfig(List<ProviderConfig> providerConfigs) {
        this.providerConfigs = providerConfigs;
    }

    public void exporter() {
        if (rpcServer == null) {
            try {
                rpcServer = new RpcServer(host + ":" + port);
            } catch (Exception e) {
                log.error("RpcServerConfig exporter exception:", e);
            }
        }

        for (ProviderConfig providerConfig : providerConfigs) {
            rpcServer.registerProcessor(providerConfig);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<ProviderConfig> getProviderConfigs() {
        return providerConfigs;
    }

    public void setProviderConfigs(List<ProviderConfig> providerConfigs) {
        this.providerConfigs = providerConfigs;
    }
}
