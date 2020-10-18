package org.rockhill.adoration.web.service;

import org.rockhill.adoration.database.tables.Social;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdorationCustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        if ((authentication != null) && (authentication.getPrincipal() instanceof GoogleUser)) {
            GoogleUser googleUser = ((GoogleUser)authentication.getPrincipal());
            Social social = googleUser.getSocial();
            if (social != null) {
                //authenticated !
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                //grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER")); //we don't need it
                Authentication auth = new PreAuthenticatedAuthenticationToken(googleUser, authentication.getCredentials(), grantedAuthorities);
                return auth; //google login success
            }
        }
        if ((authentication != null) && (authentication.getPrincipal() instanceof FacebookUser)) {
            FacebookUser facebookUser = ((FacebookUser)authentication.getPrincipal());
            Social social = facebookUser.getSocial();
            if (social != null) {
                //authenticated !
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                //grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER")); //we don't need it
                Authentication auth = new PreAuthenticatedAuthenticationToken(facebookUser, authentication.getCredentials(), grantedAuthorities);
                return auth; //facebook login success
            }
        }
        return null; //login failed
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                PreAuthenticatedAuthenticationToken.class);
    }
}