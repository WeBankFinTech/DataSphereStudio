/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.apiservice.core.token;
import org.apache.commons.codec.binary.Base64;
import com.webank.wedatasphere.dss.apiservice.core.config.ApiServiceConfiguration;
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceToken;
import org.apache.linkis.common.conf.CommonVars$;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class JwtManager {
    private static final Logger LOG = LoggerFactory.getLogger(JwtManager.class);

    private static final String CLAIM_ROLE = "role";

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final SecretKey SECRET_KEY = MacProvider.generateKey(SIGNATURE_ALGORITHM);
    private static final TemporalAmount TOKEN_VALIDITY = getTokenHour();
    private static final String JWT_SECERT = ApiServiceConfiguration.DSS_API_TOKEN_SECRET_ID.getValue();

    public final static TemporalAmount getTokenHour() {
        Properties conf = CommonVars$.MODULE$.properties();
        if (null != conf) {
            Object hour = conf.get("ujes.token.valid.hour");
            if (null == hour) {
                return Duration.ofHours(8L);
            } else {
                return Duration.ofHours(Long.parseLong(hour.toString()));
            }
        } else {
            return Duration.ofDays(365);
        }

    }

    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.decodeBase64(JWT_SECERT);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * Builds a JWT with the given subject and role and returns it as a JWS signed compact String.
     */
    public static String createToken(final String applyUser, ApiServiceToken apiServiceToken, Long duration) {
        final String role = "developer";
        final Instant now = Instant.now();
        final Date expiryDate = Date.from(now.plus(Duration.ofDays(duration)));
        Map<String, Object> tokenDetailMap = new HashMap<String, Object>();
        tokenDetailMap.put("tokenDetail", apiServiceToken);
        return Jwts.builder()
                .setSubject(applyUser)
                .claim(CLAIM_ROLE, role)
                .setClaims(tokenDetailMap)
                .setExpiration(expiryDate)
                .setIssuedAt(Date.from(now))
                .signWith(SIGNATURE_ALGORITHM, generalKey())
                .compact();
    }

    /**
     * Parses the given JWS signed compact JWT, returning the claims.
     * If this method returns without throwing an exception, the token can be trusted.
     */
    public static ApiServiceToken parseToken(final String compactToken)
            throws ExpiredJwtException,
            UnsupportedJwtException,
            MalformedJwtException,
            SignatureException,
            IllegalArgumentException {

        Jws<Claims> jws = Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(compactToken);

        Map<String, Object> tokenDetail = (Map<String, Object>) jws.getBody().get("tokenDetail");
        ApiServiceToken apiServiceToken = new ApiServiceToken();
        apiServiceToken.setApiServiceId(Long.valueOf(tokenDetail.get("apiServiceId").toString()));
        apiServiceToken.setApplyTime(null);
        apiServiceToken.setPublisher(tokenDetail.get("publisher").toString());
        apiServiceToken.setApplyUser(tokenDetail.get("applyUser").toString());

        return apiServiceToken;
    }


}
