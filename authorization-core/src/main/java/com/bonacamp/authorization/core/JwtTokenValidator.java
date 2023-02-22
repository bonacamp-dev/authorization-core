package com.bonacamp.authorization.core;

import java.lang.reflect.Array;
import java.security.Key;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.networknt.status.HttpStatus;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtTokenValidator {

	private static final String CLIENT_PREFIX = "bona-";
	private static final String CLIENT_SUFFIX = "-i";
	private static final String CLIENT_KEY = "client_id";
	private static final String AUTHORITIES_KEY = "user_role";
	private static final String SERVER_ROLE_KEY = "server_role";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_TYPE = "Bearer ";
	private static final String KEY = "67O064KY7Lqg7ZSELWNvbS1ib25hY2FtcC1hdXRob3JpemF0aW9uLWl0LXRlYW1ib25h";
    private final Key key; 
    
    public JwtTokenValidator() {
        byte[] keyBytes = Decoders.BASE64.decode(KEY);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    
    public Integer verificationToken(HttpServletRequest request) {
    	
    	String accessToken = setBearerToken(request);
    	
    	if(isNullOrEmpty(accessToken)) {
    		return HttpStatus.UNAUTHORIZED.value();
    	}
    	
    	if(!validateToken(accessToken)) {
    		return HttpStatus.UNAUTHORIZED.value();
    	}
    	
    	Claims claims = parseClaims(accessToken);
    	Object rid = claims.get(AUTHORITIES_KEY);
        String cid = new String(Decoders.BASE64.decode(claims.get(CLIENT_KEY).toString()));
        String clientId = cid.substring(5);
        clientId = clientId.substring(0, clientId.length()-2);

    	if(isNullOrEmpty(rid) || isNullOrEmpty(cid) || isNullOrEmpty(clientId)) {
    		return HttpStatus.UNAUTHORIZED.value();
    	}
    	
    	if (!cid.substring(0,5).equals(CLIENT_PREFIX) 
    			|| !cid.substring(cid.length()-2, cid.length()).equals(CLIENT_SUFFIX)) {
    		return HttpStatus.UNAUTHORIZED.value();
        }
    	
    	String serverRoles = claims.get(SERVER_ROLE_KEY).toString().replace("[", "").replace("]", "");
    	
    	if(isNullOrEmpty(serverRoles)) {
    		return HttpStatus.FORBIDDEN.value();
    	}
    	
    	String url = request.getRequestURI();
    	String method = request.getMethod().equals("GET") ? "read" : "write";
    	
    	if(serverRoles.contains(",")) {
    		String[] datas = serverRoles.split(",");
    		
    		for(String role : datas) {
    			role = role.trim();
    			
    			if(url.contains(role.substring(0, role.indexOf("."))) && method.equals(role.substring(role.indexOf(".")+1, role.length()))) {
    				return HttpStatus.OK.value();
    			}
        	}
    		return HttpStatus.FORBIDDEN.value();
    	}else {
    		if (url.contains(serverRoles.substring(0, serverRoles.indexOf("."))) && method.equals(serverRoles.substring(serverRoles.indexOf(".")+1, serverRoles.length()))) {
    			return HttpStatus.OK.value();
			}else {
				return HttpStatus.FORBIDDEN.value();
			}
    	}
    }

    private String setBearerToken(HttpServletRequest request) {

    	String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

	    if (!isNullOrEmpty(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
	      return bearerToken.substring(BEARER_TYPE.length());
	    }

	    return null;
    }
    
    private boolean validateToken(String token) {

        try {
        	Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
        }
        catch (ExpiredJwtException e) {
        }
        catch (UnsupportedJwtException e) {
        }
        catch (IllegalArgumentException e) {
        }
        catch (JwtException e) {
        }

        return false;
    }
    
    private Claims parseClaims(String token) {

        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }
        catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    
    private boolean isNullOrEmpty(Object value) {

        if (value == null) {

            return true;
        }

        if ((value instanceof String) && (((String) value).trim().length() == 0)) {

            return true;
        }

        if (value instanceof Map) {

            return ((Map<?, ?>) value).isEmpty();
        }

        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }

        if (value instanceof Optional) {
            return !((Optional<?>) value).isPresent();
        }

        if (value instanceof CharSequence) {
            return ((CharSequence) value).length() == 0;
        }

        if (value instanceof List) {

            return ((List<?>) value).isEmpty();
        }

        if (value instanceof Object[]) {

            return (((Object[]) value).length == 0);
        }

        return false;
    }
}
 
