package com.bonacamp.authorization.core.jwt;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import com.bonacamp.authorization.core.redis.service.RedisService;
import com.bonacamp.authorization.core.util.CustomUtils;
import com.bonacamp.authorization.core.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private static final String CLIENT_PREFIX = "bona-";
	private static final String CLIENT_SUFFIX = "-i";
	private static final String CLIENT_KEY = "client_id";
	private static final String AUTHORITIES_KEY = "user_role";
	private static final String SERVER_ROLE_KEY = "server_role";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_TYPE = "Bearer ";
	private static final String KEY = "67O064KY7Lqg7ZSELWNvbS1ib25hY2FtcC1hdXRob3JpemF0aW9uLWl0LXRlYW1ib25h";
	private static final String SERVERCODE = "serverCode";
	
	private final RedisService redisService;
    private Key key; 
    
    public Integer verificationToken(HttpServletRequest request, String serverCode) throws Exception {

    	byte[] keyBytes = Decoders.BASE64.decode(KEY);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        
    	String accessToken = setBearerToken(request);

    	if(CustomUtils.isNullOrEmpty(accessToken)) {
    		return HttpStatus.BAD_REQUEST.value();
    	}

    	if(!validateToken(accessToken)) {
    		return HttpStatus.UNAUTHORIZED.value();
    	}
    	
    	if(!validateRedis(accessToken)) {
    		return HttpStatus.NOT_FOUND.value();
    	}

    	if(!authorizationServer(accessToken, serverCode)) {
    		return HttpStatus.FORBIDDEN.value();
    	}

    	Claims claims = parseClaims(accessToken);
    	Object rid = claims.get(AUTHORITIES_KEY);
        String cid = new String(Decoders.BASE64.decode(claims.get(CLIENT_KEY).toString()));
        String clientId = cid.substring(5);
        clientId = clientId.substring(0, clientId.length()-2);
       
    	if(CustomUtils.isNullOrEmpty(rid) || CustomUtils.isNullOrEmpty(cid) || CustomUtils.isNullOrEmpty(clientId)) {
    		return HttpStatus.UNAUTHORIZED.value();
    	}
    	
    	if (!cid.substring(0,5).equals(CLIENT_PREFIX) || !cid.substring(cid.length()-2, cid.length()).equals(CLIENT_SUFFIX)) {
    		return HttpStatus.UNAUTHORIZED.value();
        }
    	
    	String serverRoles = claims.get(SERVER_ROLE_KEY).toString().replace("[", "").replace("]", "");

    	if(CustomUtils.isNullOrEmpty(serverRoles)) {
    		return HttpStatus.FORBIDDEN.value();
    	}
    	
    	String url = request.getRequestURI();
    	String method = request.getMethod().equals("GET") ? "read" : "write";
  
    	if(serverRoles.contains(",")) {
    		
    		String[] datas = serverRoles.split(",");
    		
    		for(String role : datas) {

    			if(checkServerRole(role, url, method)) {
    				return HttpStatus.OK.value();
    			}
        	}
    		return HttpStatus.FORBIDDEN.value();
    		
    	}else {
    		
    		if(checkServerRole(serverRoles, url, method)) {
				return HttpStatus.OK.value();
			}
			return HttpStatus.FORBIDDEN.value();
			
    	}
    }
    
    private Boolean checkServerRole(String role, String url, String method) {
    	
    	role = role.trim();
    	String roleId = getRoleId(role);
    	

    	return url.contains(role.substring(0, role.indexOf("."))) && method.equals(roleId);
    	
    }
    
    private String getRoleId(String role) {
    	
    	return role.substring(role.indexOf(".") + 1, role.length());
    	
    }
    

	private String setBearerToken(HttpServletRequest request) {

    	String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

	    if (!CustomUtils.isNullOrEmpty(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
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
    
    private boolean validateRedis(String accessToken) {

    	if(CustomUtils.isNullOrEmpty(redisService.getValue(accessToken))) {
    		return false;
    	}
    	
    	return true;
    
    }
    
    private boolean authorizationServer(String accessToken, String serverCode) throws Exception {

    	List<String> serverCodes = getServerCode(redisService.getValue(accessToken));

    	if(CustomUtils.isNullOrEmpty(serverCodes.contains(serverCode))) {
    		return false;
    	}
    	
    	return true;
    }
    
    private List<String> getServerCode(Object objAccessToken) throws Exception {
		
    	List<String> serverCodes = new ArrayList<>();
    	
    	List<Object> objectLists = Arrays.asList(new JSONParser().parse(StringUtils.writeValueAsString(objAccessToken))); 

    	JSONParser jsonParse = new JSONParser();
    	JSONArray jsonArray = (JSONArray)jsonParse.parse((String) objectLists.get(0)); 

    	for (int i = 0; i < jsonArray.size(); ++i) {
    	    JSONObject jsonObject = (JSONObject) jsonArray.get(i);   
    	    String serverCode = (String) jsonObject.get(SERVERCODE);
    	    serverCodes.add(serverCode);
    	    }

		return serverCodes;
	}
    
    private Claims parseClaims(String token) {

        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }
        catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
 
