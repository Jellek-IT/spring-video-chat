package pl.bronikowski.springchat.backendmain.config.stomp


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.bronikowski.springchat.backendmain.websocket.internal.authentication.StompAuthenticationProvider

@Configuration
class StompTestConfig {
    @Bean
    @Primary
    StompAuthenticationProvider stompAuthenticationProvider() {
        return new StompTestAuthenticationProvider()
    }
}
