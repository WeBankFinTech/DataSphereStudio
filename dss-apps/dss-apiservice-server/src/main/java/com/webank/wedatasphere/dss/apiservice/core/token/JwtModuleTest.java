/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.apiservice.core.token;

import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceToken;
import io.jsonwebtoken.*;

import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.util.logging.Logger;


public class JwtModuleTest {

    private static final Logger LOG = Logger.getLogger( JwtModuleTest.class.getName() );

    public  static void main(String args[]){

        ApiServiceToken apiServiceToken = new ApiServiceToken();
        apiServiceToken.setApplyUser("allenlliu");
        apiServiceToken.setApiServiceId(110L);

       String token =  JwtManager.createToken("allenlliu",apiServiceToken,30L);
        try {
            parse(token);
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void parse(String jwtToken) throws LoginException {
      // String jwt = getJwt();

        if ( jwtToken != null ) {
            try {
                LOG.info( "JWT provided" );
               // Date i = CommonVars$.MODULE$.apply("", new TimeType("3h")).getValue().toDate();
               // JwtManager jwtManager = lookupJwtManager();

                // verify the received token
                ApiServiceToken tokenDetail = JwtManager.parseToken( jwtToken );

                // now we can trust its information...
//                String user = jws.getBody().getSubject();
                //identity = new SimplePrincipal( user );

//                String role = (String) jws.getBody().get( "role" );

//                Map<String,Object> tokenDetail = (Map<String,Object>)jws.getBody().get("tokenDetail");
                //group = new SimplePrincipal( role );

//                Map<String,Object> details = jws.getBody().get("tokenDetail");

                LOG.info( "JWT is valid, logging in user " + tokenDetail.getApplyUser() );


            } catch ( SignatureException | MalformedJwtException
                    | UnsupportedJwtException | IllegalArgumentException e ) {
                throw new FailedLoginException( "Invalid security token provided" );
            } catch ( ExpiredJwtException e ) {
                throw new CredentialExpiredException( "The security token is expired" );
            }
        }

    }

//    @Override
//    public boolean commit() throws LoginException {
//        Set<Principal> principals = subject.getPrincipals();
//        principals.add( identity );
//
//        SimpleGroup roles = new SimpleGroup( "Roles" );
//        roles.addMember( group );
//        principals.add( roles );
//
//        return true;
//    }

//    public JwtManager lookupJwtManager() {
//        try {
//            BeanManager beanManager = InitialContext.doLookup( "java:comp/BeanManager" );
//            Set<Bean<?>> beans = beanManager.getBeans( JwtManager.class );
//            if ( beans.isEmpty() ) {
//                throw new RuntimeException( "Failed looking up CDI Bean " + JwtManager.class.getName()
//                                                    + ": Found " + beans.size() + " " );
//            }
//            Bean<?> bean = beans.iterator().next();
//            CreationalContext<?> context = beanManager.createCreationalContext( bean );
//            return (JwtManager) beanManager.getReference( bean, JwtManager.class, context );
//        } catch ( NamingException e ) {
//            throw new RuntimeException( e );
//        }
//    }
}
