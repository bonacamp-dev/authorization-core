package com.bonacamp.authorization.core.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.bonacamp.authorization.core.configuration")
@ComponentScan("com.bonacamp.authorization.core.jwt")
@ComponentScan("com.bonacamp.authorization.core.redis.service")
@ComponentScan("com.bonacamp.authorization.core.util")
public class AutoConfig {

}
