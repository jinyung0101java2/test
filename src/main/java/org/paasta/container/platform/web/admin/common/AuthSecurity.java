package org.paasta.container.platform.web.admin.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component("authSecurity")
public class AuthSecurity {
    public boolean checkisGlobalAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Constants.AUTH_USER))) {
            return false;
        }
        return true;
    }
}
