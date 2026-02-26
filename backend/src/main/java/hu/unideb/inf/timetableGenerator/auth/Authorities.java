package hu.unideb.inf.timetableGenerator.auth;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class Authorities {

    private enum AuthorityTypes{
        ROLE_USER,
        ROLE_ADMIN
    }

    public static class UserAuthority implements GrantedAuthority {
        @Override
        public @Nullable String getAuthority() {
            return "ROLE_USER";
        }
    }

    public static  class AdminAuthority implements GrantedAuthority {
        @Override
        public @Nullable String getAuthority() {
            return "ROLE_ADMIN";
        }
    }

    public static GrantedAuthority getGrantedAuthority(String authority) throws IllegalArgumentException {
        return switch (AuthorityTypes.valueOf(authority)) {
            case ROLE_USER -> new UserAuthority();
            case ROLE_ADMIN -> new AdminAuthority();
        };
    }

    public static List<GrantedAuthority> getGrantedAuthorities(String authorities) throws IllegalArgumentException {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for( String authority : authorities.split(",")){
            grantedAuthorities.add(getGrantedAuthority(authority));
        }
        return grantedAuthorities;
    }
}
