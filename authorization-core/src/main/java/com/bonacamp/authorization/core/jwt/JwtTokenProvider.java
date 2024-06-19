package com.bonacamp.authorization.core.jwt;

import com.bonacamp.authorization.core.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.List;


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
		initializeKey();
		String accessToken = extractBearerToken(request);
		if (accessToken == null || !validateToken(accessToken) || !isTokenValidInRedis(accessToken)) {
			return HttpStatus.UNAUTHORIZED.value();
		}

		if (!isAuthorizedServer(accessToken, serverCode)) {
			return HttpStatus.FORBIDDEN.value();
		}

		Claims claims = parseClaims(accessToken);
		if (!areClaimsValid(claims)) {
			return HttpStatus.UNAUTHORIZED.value();
		}

		String serverRoles = extractServerRoles(claims);
		if (serverRoles == null) {
			return HttpStatus.FORBIDDEN.value();
		}

		return isRoleAuthorized(serverRoles, request.getRequestURI(), request.getMethod()) ? HttpStatus.OK.value() : HttpStatus.FORBIDDEN.value();
	}

	private void initializeKey() {
		byte[] keyBytes = Decoders.BASE64.decode(KEY);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	private String extractBearerToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		return (bearerToken != null && bearerToken.startsWith(BEARER_TYPE)) ? bearerToken.substring(BEARER_TYPE.length()) : null;
	}

	private boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}

	private boolean isTokenValidInRedis(String accessToken) {
		return redisService.getValue(accessToken) != null;
	}

	private boolean isAuthorizedServer(String accessToken, String serverCode) {
		List<String> serverCodes = extractServerCodesFromToken(accessToken);
		return serverCodes.contains(serverCode);
	}

	private List<String> extractServerCodesFromToken(String accessToken) {
		Object objAccessToken = redisService.getValue(accessToken);
		return (List<String>) objAccessToken;
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	private boolean areClaimsValid(Claims claims) {
		String cid = new String(Decoders.BASE64.decode(claims.get(CLIENT_KEY).toString()));
		String clientId = cid.substring(5, cid.length() - 2);
		return claims.get(AUTHORITIES_KEY) != null && isClientIdValid(cid, clientId);
	}

	private boolean isClientIdValid(String cid, String clientId) {
		return cid.startsWith(CLIENT_PREFIX) && cid.endsWith(CLIENT_SUFFIX) && clientId != null;
	}

	private String extractServerRoles(Claims claims) {
		Object serverRolesObj = claims.get(SERVER_ROLE_KEY);
		return serverRolesObj != null ? serverRolesObj.toString().replace("[", "").replace("]", "") : null;
	}

	private boolean isRoleAuthorized(String serverRoles, String url, String method) {
		String[] roles = serverRoles.split(",");
		String httpMethod = method.equals("GET") ? "read" : "write";
		return Arrays.stream(roles)
				.map(String::trim)
				.anyMatch(role -> url.contains(role.split("\\.")[0]) && httpMethod.equals(role.split("\\.")[1]));
	}
}
 
