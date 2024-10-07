package tingeso.prestabanco.security;

import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tingeso.prestabanco.dto.LoginRequest;
import tingeso.prestabanco.model.ClientModel;
import tingeso.prestabanco.model.UserModel;
import tingeso.prestabanco.repository.ClientRepository;
import tingeso.prestabanco.repository.UserRepository;
import tingeso.prestabanco.service.ClientService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class ClientAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        System.out.println("autenticando");
        String email = auth.getName();
        String password = auth.getCredentials().toString();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();



        Optional<UserModel> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            System.out.println("NO ACCOUNT");
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.get().getAuthorities().containsAll(authorities)) {}

        System.out.println("hay email");
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        System.out.println("hay password");
        return new UsernamePasswordAuthenticationToken(user, password, user.get().getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
