package cn.cyf.chatclient.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类，用于生成和解析 JWT 令牌
 */
public class JwtUtils {
    private static final String SECRET_KEY = "SVRIRUlNQQ==";
    // 令牌过期时间：1 小时（单位：毫秒）
    private static final long EXPIRATION = 60 * 60 * 1000 * 24; // 1小时

    /**
     * 生成 JWT 令牌
     *
     * @param claims 要包含在令牌中的自定义声明（如用户ID、用户名等）
     * @return JWT 令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims) // 添加自定义声明
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 使用 HS256 算法签名
                .compact();
    }

    /**
     * 解析 JWT 令牌
     *
     * @param token JWT 令牌字符串
     * @return 解析后的 Claims 对象，包含令牌中的信息
     * @throws io.jsonwebtoken.JwtException 如果令牌无效或签名不匹配
     */
    public static Claims parseToken(String token) {
        // 移除 Bearer 前缀（如果存在）
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 从令牌中获取用户角色
     */
    public static String getUserRole(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("role");
    }
    
    /**
     * 从令牌中获取用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("username");
    }
    
    /**
     * 从令牌中获取用户ID
     */
    public static Integer getUserId(String token) {
        Claims claims = parseToken(token);
        return (Integer) claims.get("id");
    }
}
