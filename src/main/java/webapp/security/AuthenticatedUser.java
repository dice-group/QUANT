package webapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import webapp.model.User;

import java.util.Collection;

public class AuthenticatedUser extends User implements UserDetails {
    protected AuthenticatedUser(User user) {
        super(user.getEmail(),user.getPassword(),user.getRole());
    }
    @Override
    public String getUsername(){
        return this.getEmail();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(this.getRole().toString());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
