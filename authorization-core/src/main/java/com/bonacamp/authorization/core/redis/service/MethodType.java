package com.bonacamp.authorization.core.redis.service;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MethodType {

    READ("READ"),
    WRITE("WRITE")
    ;

    private final String value;
    
    public static MethodType of(String code) {
    	
        return Arrays.stream(MethodType.values())
                .filter(value -> value.check(code.toUpperCase()))
                .findAny()
                .orElseThrow();
    }

    private boolean check(String code) {
    	
    	if(value.equals(code)) {
    		return true;
    	}

        return false;
    }
    
}