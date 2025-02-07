package pl.bronikowski.springchat.backendmain

import com.fasterxml.jackson.databind.ObjectMapper
import com.redis.testcontainers.RedisContainer
import jakarta.ws.rs.core.Response
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RoleMappingResource
import org.keycloak.admin.client.resource.RoleResource
import org.keycloak.admin.client.resource.RoleScopeResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.RoleRepresentation
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles
import pl.bronikowski.springchat.backendmain.channel.internal.file.ChannelFileRepository
import pl.bronikowski.springchat.backendmain.channel.internal.message.ChannelMessageRepository
import pl.bronikowski.springchat.backendmain.config.Profiles
import pl.bronikowski.springchat.backendmain.config.stomp.StompTestClient
import pl.bronikowski.springchat.backendmain.config.stomp.WithMockStompUserTestExecutionListener
import pl.bronikowski.springchat.backendmain.member.internal.MemberRepository
import pl.bronikowski.springchat.backendmain.shared.constants.DateTimeConstants
import pl.bronikowski.springchat.backendmain.storage.internal.StorageFileRepository
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.JanusWebSocketClient
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import spock.lang.Shared
import spock.lang.Specification
import spock.util.time.MutableClock

import java.time.Clock

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestExecutionListeners(mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS, listeners = [
        WithMockStompUserTestExecutionListener.class
])
@ActiveProfiles(Profiles.TEST)
class BaseSpecification extends Specification {
    private static final String POSTGRES_IMAGE_NAME = "postgres:17.2"
    private static final String REDIS_IMAGE_NAME = "redis:7.4.2-alpine"
    private static final String RABBITMQ_IMAGE_NAME = 'rabbitmq:4.0.4-management-alpine'
    private static final Integer RABBITMQ_STOMP_PORT = 61613

    @Shared
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer(POSTGRES_IMAGE_NAME)
    @Shared
    static RedisContainer redisContainer = new RedisContainer(REDIS_IMAGE_NAME)

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    MemberRepository memberRepository

    @Autowired
    StorageFileRepository storageFileRepository

    @Autowired
    StompTestClient stompTestClient

    @Autowired
    ChannelMessageRepository channelMessageRepository

    @Autowired
    ChannelFileRepository channelFileRepository

    @SpringBean
    Clock clock = new MutableClock(TestConstants.NOW, DateTimeConstants.DEFAULT_ZONE_OFFSET)

    @SpringBean
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter()

    @SpringBean
    JanusWebSocketClient janusWebSocketClient = Mock()

    @SpringBean
    Keycloak keycloak = Mock()

    @SpringBean
    S3Client s3Client = Mock()

    static {
        postgresContainer.start()
        System.setProperty("test.containers.postgres.url", postgresContainer.jdbcUrl)
        System.setProperty("test.containers.postgres.username", postgresContainer.username)
        System.setProperty("test.containers.postgres.password", postgresContainer.password)
        redisContainer.start()
        System.setProperty("test.containers.redis.host", redisContainer.host)
        System.setProperty("test.containers.redis.port", redisContainer.redisPort.toString())
        System.setProperty("test.containers.redis.password", "")
    }

    def mockKeycloakCreateResponse(int createInvocations = 1) {
        keycloak.realm(_) >> Mock(RealmResource) {
            users() >> Mock(UsersResource) {
                createInvocations * create(_) >> Mock(Response) {
                    getStatus() >> 201
                    getStatusInfo() >> Response.Status.CREATED
                    getLocation() >> URI.create("http://localhost:8090/admin/realms/test/users/${UUID.randomUUID().toString()}")
                }
                get(_) >> Mock(UserResource) {
                    roles() >> Mock(RoleMappingResource) {
                        realmLevel() >> Mock(RoleScopeResource) {
                            listAll() >> List.of(Mock(RoleRepresentation) {
                                getName() >> Roles.MEMBER
                            })
                        }
                    }
                }
            }
            roles() >> Mock(RolesResource) {
                it.get(_) >> Mock(RoleResource) {
                    toRepresentation() >> Mock(RoleRepresentation)
                }
            }
        }
    }

    def mockS3ClientGetObjectResponse() {
        s3Client.getObject(_) >> new ResponseInputStream<GetObjectResponse>(
                Mock(GetObjectResponse) {
                    contentLength() >> 8
                    contentType() >> "image/jpeg"
                },
                new ByteArrayInputStream("response".getBytes()))
    }
}
