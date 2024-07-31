package example;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity){
        try {
            httpSecurity
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/login","/hello").permitAll()
                            .anyRequest().authenticated())
                    .formLogin(Customizer.withDefaults());
            return httpSecurity.build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails user = User.withUsername("username").password(passwordEncoder().encode("password")).build();
        return new InMemoryUserDetailsManager(user);
    }
}
