package com.externalize.mock.config;

import com.externalize.mock.model.MockCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

@Configuration
public class MockConfig {
    @Value("${AUDIT_LOG_SERVICE_ENDPT_CONFIG}")
    String audithost;

    @Bean("MOCK_CASES")
    @ConfigurationProperties(prefix = "mock.case")
    public List<MockCase> mockCaseList() {
        System.setProperty("AUDIT_LOG_SERVICE_ENDPT_CONFIG", audithost);
        return new LinkedList<>();
    }
}
