package org.rockhill.adorApp.web.service;

import org.rockhill.adorApp.database.business.BusinessWithPeople;
import org.rockhill.adorApp.database.tables.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdorationCustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    BusinessWithPeople businessWithPeople;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        //check if we have proper e-mail
        People people = businessWithPeople.getPersonByEmail(name.toLowerCase()); //we do not care about password now.
        if (people != null) {
            //authenticated !
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuthorities);
            return auth; //login success
        }
        return null; //login failed
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}