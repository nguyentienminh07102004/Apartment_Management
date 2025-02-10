package com.ptitB22CN539.LaptopShop.Config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtGenerator {
    @Value(value = "${signerKey}")
    private String signingKey;

    public JwtEntity jwtGenerator(UserEntity user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String jwtId = UUID.randomUUID().toString();
        Date expiredDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .expirationTime(expiredDate)
                .jwtID(jwtId)
                .issueTime(new Date(System.currentTimeMillis()))
                .issuer(ConstantConfig.NAME)
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signingKey.getBytes()));
            return new JwtEntity(jwtId, jwsObject.serialize(), expiredDate, user);
        } catch (JOSEException e) {
            throw new DataInvalidException(ExceptionVariable.SERVER_ERROR);
        }
    }

    public boolean verify(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        signedJWT.verify(new MACVerifier(signingKey.getBytes()));
        Date expired = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expired.after(Date.from(Instant.now()))) {
            throw new DataInvalidException(ExceptionVariable.TOKEN_INVALID);
        }
        return signedJWT.getJWTClaimsSet().getSubject()
                .equals(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
