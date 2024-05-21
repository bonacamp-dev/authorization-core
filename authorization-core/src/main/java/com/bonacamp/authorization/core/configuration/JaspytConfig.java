package com.bonacamp.authorization.core.configuration;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JaspytConfig {
    @Value("${server.key}")
    private String key;
    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(@Value("${server.key}") String password) {

        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();

        SimpleStringPBEConfig simpleStringPBEConfig = new SimpleStringPBEConfig();
        simpleStringPBEConfig.setPassword(password);
        simpleStringPBEConfig.setAlgorithm("PBEWithMD5AndDES");
        simpleStringPBEConfig.setPoolSize(1);
        simpleStringPBEConfig.setProviderName("SunJCE");
        simpleStringPBEConfig.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        simpleStringPBEConfig.setStringOutputType("base64");
        pooledPBEStringEncryptor.setConfig(simpleStringPBEConfig);
        return pooledPBEStringEncryptor;
    }
}
