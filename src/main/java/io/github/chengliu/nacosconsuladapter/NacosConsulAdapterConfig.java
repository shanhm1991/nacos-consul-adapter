package io.github.chengliu.nacosconsuladapter;

import io.github.chengliu.nacosconsuladapter.config.NacosConsulAdapterProperties;
import io.github.chengliu.nacosconsuladapter.controller.AgentController;
import io.github.chengliu.nacosconsuladapter.controller.ServiceController;
import io.github.chengliu.nacosconsuladapter.service.RegistrationService;
import io.github.chengliu.nacosconsuladapter.service.impl.DirectRegistrationService;
import io.github.chengliu.nacosconsuladapter.service.impl.LongPollingRegistrationService;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

@EnableConfigurationProperties
@Slf4j
public class NacosConsulAdapterConfig {

    @Bean
    @ConditionalOnMissingBean
    public NacosConsulAdapterProperties nacosConsulAdapterProperties() {
        return new NacosConsulAdapterProperties();
    }


    @Bean
    public RegistrationService registrationService(NacosConsulAdapterProperties nacosConsulAdapterProperties,
                                                   DiscoveryClient discoveryClient, NacosServiceManager nacosServiceManager,
                                                   NacosDiscoveryProperties nacosDiscoveryProperties, ReactiveDiscoveryClient reactiveDiscoveryClient) {
        if (NacosConsulAdapterProperties.DIRECT_MODE.equals(nacosConsulAdapterProperties.getMode())) {
            return new DirectRegistrationService(reactiveDiscoveryClient);
        }
        return new LongPollingRegistrationService(nacosConsulAdapterProperties, discoveryClient, nacosServiceManager, nacosDiscoveryProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public AgentController agentController() {
        return new AgentController();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("nacosConsulAdapterProperties")
    public ServiceController serviceController(RegistrationService registrationService) {
        return new ServiceController(registrationService);
    }

}
