package com.dimka228.messenger.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableDiscoveryClient
@ConditionalOnProperty({ "messenger.service-discovery" })
public class ServiceDiscoveryConfig {

}
