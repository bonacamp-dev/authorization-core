package com.bonacamp.authorization.core.configuration;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.bonacamp.authorization.core.configuration")
@ComponentScan("com.bonacamp.authorization.core.jwt")
@ComponentScan("com.bonacamp.authorization.core.redis.service")
@ComponentScan("com.bonacamp.authorization.core.util")
public class AutoConfig {

}
