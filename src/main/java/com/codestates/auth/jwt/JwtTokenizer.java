package com.codestates.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenizer {
    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public String encodedBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }//시크릿키를 암호화합니다 왜냐하ㅏ면 평문으로 보내는걸 권장하지않기떄문에(전달과정에서 탈취의 위험이 있으니까 항상 인코딩해서 쓰자!)

    public String generateAccessToken(Map<String, Object> claims,
                                      String subject,
                                      Date expiration,
                                      String base64EncodedSecretKey) {//시크릿키 인코딩된 값
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);//시크릿키 인코딩-> 디코딩된값

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String subject, Date expiration, String base64EncodedKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedKey);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);//입력받은 토큰을 파싱해서 claims를 얻는다.(ㄴㅐ부적으로 시그니쳐검증을 시도한다)
        return claims;//JWT에서 Claims를 파싱 할 수 있다는 의미는 내부적으로 서명(Signature) 검증에 성공했다는 의미
    }

    public void verifySignature(String jws, String base64EncodedSecretKey){
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }

    public Date getTokenExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();//현재시간가져오기
        calendar.add(Calendar.MINUTE, expirationMinutes);//현재시간에 + 만료시간 추가
        Date expiration = calendar.getTime();//만료되는시점의 시간을 가져온다.

        return expiration;//30분을 설정하면 -> 지금부터 30분 후의 시간반환
    }

    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);//디코딩해서 바이트 배열로 받아야함
        //해시알고리즘은 키의 길이나 형태에 특별한 요구사항이 있다. 인코딩하면 길이가 길어지기때문에 요구사항을 충족못할수도있기때문에
        //디코딩해서 사용해야한다고 봐도 무관한것같다.!
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return key;
    }
}
