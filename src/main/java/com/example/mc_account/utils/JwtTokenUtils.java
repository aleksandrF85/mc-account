package com.example.mc_account.utils;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class JwtTokenUtils {

    @SneakyThrows
    public Map<String, Object> parseJwtToken(String bearerToken) {

        JWT jwt = JWTParser.parse(bearerToken.replaceFirst("Bearer ", ""));
        Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();

        return claims;
    }
}
