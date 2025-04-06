package pl.bronikowski.springchat.backendnotifications


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.bronikowski.springchat.backendnotifications.config.Profiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles(Profiles.TEST)
class BaseSpecification extends Specification {
    private static final String MONGODB_IMAGE_NAME = "mongo:6.0.21"

    /**
     * todo: implement some tests
     */
}
