package com.lms.securitry;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lms.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {
	
	private final SecretKey key;
	   private final long accessExpiration;
	    private final long refreshExpiration;
	    
	    
	    
	
	public JwtService(@Value ("${app.jwt.secret}") String secret, 
			 @Value("${app.jwt.access-expiration}") long accessExpiration,
			 @Value("${app.jwt.refresh-expiration}") long refreshExpiration) {
			super();
			this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
			this.accessExpiration = accessExpiration;
			this.refreshExpiration = refreshExpiration;
		}
	
	
	public String accessToken(User user) {
		return create(user, accessExpiration);
	}

	
	public String refreshToken(User user) {
		return create(user, refreshExpiration);
	}



	  private String create(User user, long expiration) {
	        Date now = new Date();
	        return Jwts.builder().setSubject(user.getEmail().toString())
	                .setIssuedAt(now).setExpiration(new Date(now.getTime() + expiration))
	                .signWith(key, SignatureAlgorithm.HS256).compact();
	    }
	  
	  
	  public String getEmail(String token) {
//	        return Long.valueOf(Jwts.parserBuilder().setSigningKey(key).build()
//	                .parseClaimsJws(token).getBody().getSubject());
//	   
		 return Jwts.parserBuilder().setSigningKey(key).build()
				 .parseClaimsJws(token).getBody().getSubject(); 
	  
	  
	  }

}
