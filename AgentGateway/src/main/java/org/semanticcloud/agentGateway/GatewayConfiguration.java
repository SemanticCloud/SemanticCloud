package org.semanticcloud.agentGateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway")
@Getter
@Setter
public class GatewayConfiguration {
    private String platformId;
    private String mainPort;
    private String mainHost;
    private String containerName;
    private String localhost;
    private String localPort;
    private String services;
}
