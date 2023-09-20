package org.container.platform.web.ui.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component("authSecurity")
public class AuthSecurity {
    public boolean checkIsGlobal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Constants.AUTH_SUPER_ADMIN))) {
            return true;
        }
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Constants.AUTH_CLUSTER_ADMIN))) {
            return true;
        }
        return false;
    }


    public boolean checkIsClusterAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Constants.AUTH_USER))) {
            return false;
        }
        return true;
    }


    public boolean checkIsSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Constants.AUTH_SUPER_ADMIN))) {
            return true;
        }
        return false;
    }
}
