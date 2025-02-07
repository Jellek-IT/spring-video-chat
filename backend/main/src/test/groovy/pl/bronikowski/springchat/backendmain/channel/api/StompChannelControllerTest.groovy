package pl.bronikowski.springchat.backendmain.channel.api


import org.springframework.test.context.jdbc.Sql
import pl.bronikowski.springchat.backendmain.BaseSpecification
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles
import pl.bronikowski.springchat.backendmain.channel.api.dto.file.ChannelFileIdDto
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.ChannelMessageBasicsDto
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.CreateChannelMessagePayload
import pl.bronikowski.springchat.backendmain.config.stomp.WithMockStompUser
import pl.bronikowski.springchat.backendmain.exception.ExceptionResponse
import pl.bronikowski.springchat.backendmain.websocket.api.StompDestinations

import java.time.Instant

@Sql("classpath:sql/Member.sql")
@Sql("classpath:sql/Channel.sql")
@Sql(value = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class StompChannelControllerTest extends BaseSpecification {
    private static final String CHANNEL_ID = "00000000-0000-0000-0000-000000000001"
    private static final String DELETED_MEMBER_CHANNEL_ID = "00000000-0000-0000-0000-000000000002"
    private static final String MEMBER_ID = "00000000-0000-0000-0000-000000000001"
    private static final String NOT_ASSIGNED_MEMBER_ID = "00000000-0000-0000-0000-000000000002"

    @WithMockStompUser(roles = Roles.MEMBER, value = MEMBER_ID)
    def "should create message"() {
        when: "stomp message is sent"
        var payload = new CreateChannelMessagePayload("Test message", List.of(
                new ChannelFileIdDto(UUID.fromString('00000000-0000-0000-0000-000000000001'))
        ))
        var response = stompTestClient.sendWithExpectedResponse(
                "/app/channels.${CHANNEL_ID}.create-message",
                "/topic/channels.${CHANNEL_ID}.message", payload, ChannelMessageBasicsDto.class)

        then: "subscription response is valid"
        response.id != null
        response.createdAt == clock.instant()
        response.text == "Test message"
        response.member.id() == UUID.fromString(MEMBER_ID)
        response.member.nickname() == "Sonia"
        response.member.hasProfilePicture()
        response.files.size() == 1
        response.files[0].id() == UUID.fromString("00000000-0000-0000-0000-000000000001")
        response.files[0].createdAt() == Instant.parse("2025-01-01T00:00:00Z")
        response.files[0].type() == ChannelFileType.IMAGE

        and: "message was persisted in db"
        var message = channelMessageRepository.findById(response.id).orElseThrow()
        message.createdAt == clock.instant()
        message.text == "Test message"
        message.sequence == 1
        message.channel.id == UUID.fromString(CHANNEL_ID)
    }

    @WithMockStompUser(roles = Roles.MEMBER, value = NOT_ASSIGNED_MEMBER_ID)
    def "should not allow to create message for member without rights to channel"() {
        when: "stomp message is sent"
        var payload = new CreateChannelMessagePayload("Test message", List.of())
        var response = stompTestClient.sendWithExpectedResponse(
                "/app/channels.${CHANNEL_ID}.create-message",
                StompDestinations.USER_PREFIX + StompDestinations.ERRORS_QUEUE_DESTINATION, payload,
                ExceptionResponse.class)

        then: "valid error types are present"
        response.types.collect { it.type() }.sort() == ["STOMP_DESTINATION_FORBIDDEN"]

        where:
        channelId << [CHANNEL_ID, // member 2 without right to send
                      DELETED_MEMBER_CHANNEL_ID] // deleted member 2
    }

    @WithMockStompUser(roles = Roles.MEMBER, value = MEMBER_ID)
    def "should not allow to create message for invalid payload"() {
        when: "stomp message is sent"
        var files = filesId.collect { it -> new ChannelFileIdDto(UUID.fromString(it)) }
        var payload = new CreateChannelMessagePayload(text, files)
        var response = stompTestClient.sendWithExpectedResponse(
                "/app/channels.${CHANNEL_ID}.create-message",
                StompDestinations.USER_PREFIX + StompDestinations.ERRORS_QUEUE_DESTINATION, payload,
                ExceptionResponse.class)

        then: "valid error types are present"
        response.types.collect { it.type() }.sort() == types

        where:
        text        | filesId                                         | types
        "  "        | List.of()                                       | ['CHANNEL_MESSAGE_WITH_CONTENT']
        ""          | List.of('00000000-0000-0000-0000-000000000002') | ['NOT_FOUND']
        "a" * 10001 | List.of()                                       | ['SIZE']
    }
}
