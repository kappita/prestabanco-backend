package tingeso.prestabanco.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tingeso.prestabanco.security.ExecutiveJwtAuthenticationFilter;
import tingeso.prestabanco.security.UserAuthenticationProvider;
import tingeso.prestabanco.security.ClientJwtAuthenticationFilter;
import tingeso.prestabanco.security.UserJwtAuthenticationFilter;
//import tingeso.prestabanco.security.ExecutiveAuthenticationProvider;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private UserJwtAuthenticationFilter userJwtAuthenticationFilter;

    @Autowired
    private ClientJwtAuthenticationFilter clientJwtAuthenticationFilter;

    @Autowired
    private ExecutiveJwtAuthenticationFilter executiveJwtAuthenticationFilter;


    @Bean
    public AuthenticationManager userAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(userAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/clients/**")
                .securityMatcher("/mortgage_loan/**")
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/clients/register", "/clients/login").permitAll()
                .requestMatchers("/clients/**",
                                 "/mortgage_loan/*/cancel_mortgage",
                                 "/mortgage_loan/*/add_documents",
                                 "/mortgage_loan/*/set_final_approval",
                                 "/mortgage_loan",
                                 "/simulator").authenticated())
                .addFilterBefore(clientJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }


    @Bean
    @Order(3)
    public SecurityFilterChain executiveSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/executives/**")
                .securityMatcher("/mortgage_loan/**")
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/executives/login",
                                "/executives/register").permitAll()
                .requestMatchers("/executives/**").authenticated()
                        .requestMatchers("/mortgage_loan/*/set_pending_documentation").authenticated())
                .addFilterBefore(executiveJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain commonSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/mortgage_loan/*")
                .securityMatcher("/utils/loan_types")
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/mortgage_loan/*").hasAnyRole("CLIENT", "EXECUTIVE"))
                .addFilterBefore(userJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     configuration.addAllowedOrigin("*");
    //     configuration.addAllowedMethod("*");
    //     configuration.addAllowedHeader("*");
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }



}
