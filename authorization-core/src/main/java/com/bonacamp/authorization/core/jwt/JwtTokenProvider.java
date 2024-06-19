package com.bonacamp.authorization.core.jwt;

import com.bonacamp.authorization.core.redis.service.RedisService;
import com.bonacamp.authorization.core.util.CustomUtils;
import com.bonacamp.authorization.core.util.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private static final String CLIENT_PREFIX = "bona-";
	private static final String CLIENT_SUFFIX = "-i";
	private static final String CLIENT_KEY = "client_id";
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

		if (accessToken == null) { return HttpStatus.BAD_REQUEST.value(); }
		if (!validateToken(accessToken)) { return HttpStatus.UNAUTHORIZED.value(); }
		if (!isTokenInRedis(accessToken))  { return HttpStatus.NOT_FOUND.value(); }
		if (!isAuthorizedServer(accessToken, serverCode)) { return HttpStatus.FORBIDDEN.value(); }

		Claims claims = parseClaims(accessToken);
		String clientId = getClientId(claims);

		if (clientId == null || !isValidClientId(clientId)) { return HttpStatus.UNAUTHORIZED.value(); }

		String serverRoles = extractServerRoles(claims);
		if (serverRoles == null) { return HttpStatus.FORBIDDEN.value(); }

		String url = request.getRequestURI();
		String method = getRequestMethod(request);

		return isRoleAuthorized(serverRoles, url, method) ? HttpStatus.OK.value() : HttpStatus.FORBIDDEN.value();
	}

	private void initializeKey() {
		byte[] keyBytes = Decoders.BASE64.decode(KEY);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	private String extractBearerToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		return (!CustomUtils.isNullOrEmpty(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) ?
				bearerToken.substring(BEARER_TYPE.length()) : null;
	}

	private boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}

	private boolean isTokenInRedis(String accessToken) {
		return !CustomUtils.isNullOrEmpty(redisService.getValue(accessToken));
	}

	private boolean isAuthorizedServer(String accessToken, String serverCode) throws Exception {
		List<String> serverCodes = getServerCodes(redisService.getValue(accessToken));
		return serverCodes.contains(serverCode);
	}

	private List<String> getServerCodes(Object accessToken) throws Exception {
		JSONArray jsonArray = (JSONArray) new JSONParser().parse(StringUtils.writeValueAsString(accessToken));
		List<String> serverCodes = new ArrayList<>();

		for (Object obj : jsonArray) {
			JSONObject jsonObject = (JSONObject) obj;
			serverCodes.add((String) jsonObject.get(SERVERCODE));
		}
		return serverCodes;
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	private String getClientId(Claims claims) {
		String cid = new String(Decoders.BASE64.decode(claims.get(CLIENT_KEY).toString()));
		return CustomUtils.isNullOrEmpty(cid) ? null : cid.substring(5, cid.length() - 2);
	}

	private boolean isValidClientId(String clientId) {
		return clientId.startsWith(CLIENT_PREFIX) && clientId.endsWith(CLIENT_SUFFIX);
	}

	private String extractServerRoles(Claims claims) {
		Object serverRoleObj = claims.get(SERVER_ROLE_KEY);
		return serverRoleObj != null ? serverRoleObj.toString().replace("[", "").replace("]", "") : null;
	}

	private String getRequestMethod(HttpServletRequest request) {
		return request.getMethod().equals("GET") ? "read" : "write";
	}

	private boolean isRoleAuthorized(String serverRoles, String url, String method) {
		for (String role : serverRoles.split(",")) {
			if (isRoleValid(role.trim(), url, method)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRoleValid(String role, String url, String method) {
		String roleId = role.substring(role.indexOf(".") + 1);
		return url.contains(role.substring(0, role.indexOf("."))) && method.equals(roleId);
	}
}
 
