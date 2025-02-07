package pl.bronikowski.springchat.backendmain.member.api

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import pl.bronikowski.springchat.backendmain.BaseSpecification
import pl.bronikowski.springchat.backendmain.config.objectmapper.ObjectMapperConfig
import pl.bronikowski.springchat.backendmain.exception.ExceptionResponse
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberBasicsDto
import pl.bronikowski.springchat.backendmain.user.api.UserType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@Sql("classpath:sql/Member.sql")
@Sql(value = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PublicMemberControllerTest extends BaseSpecification {
    static final endpointUrl = "/public/members"

    def "should create member"() {
        given: "keycloak create response"
        mockKeycloakCreateResponse(1)

        when: "request is sent"
        var response = mvc.perform(registerRequest())
                .andReturn()
                .response

        then: "response status is 200 ok"
        response.status == HttpStatus.OK.value()

        and: "response data is valid"
        var memberDto = objectMapper.readValue(response.contentAsString, MemberBasicsDto)
        memberDto.id() != null
        memberDto.nickname() == "New"
        !memberDto.hasProfilePicture()

        and: "member was created"
        var member = memberRepository.findById(memberDto.id()).orElseThrow()
        member.nickname == "New"
        member.authResourceId != null
        member.createdAt == clock.instant()
        member.email == "new@example.com"
        member.registerEmail == "new@example.com"
        member.type == UserType.MEMBER
        member.emailVerified
        member.profilePicture == null
    }

    def "should not allow to create member for invalid payload"() {
        given: "keycloak create response"
        mockKeycloakCreateResponse(0)

        when: "request is sent"
        var response = mvc.perform(registerRequest(email, nickname, password))
                .andReturn()
                .response

        then: "response type is valid"
        response.status == responseStatus.value()

        and: "valid error types are present"
        def exceptionResponse = objectMapper.readValue(response.contentAsString, ExceptionResponse)
        exceptionResponse.types.collect { it.type() }.sort() == errorTypes

        where:
        email               | nickname      | password        | responseStatus         | errorTypes
        ""                  | "New"         | "Password_1234" | HttpStatus.BAD_REQUEST | ["NOT_EMPTY"]
        "invalid"           | "New"         | "Password_1234" | HttpStatus.BAD_REQUEST | ["EMAIL"]
        "sonia@example.com" | "New"         | "Password_1234" | HttpStatus.BAD_REQUEST | ["UNIQUE_EMAIL"]
        "new@example.com"   | ""            | "Password_1234" | HttpStatus.BAD_REQUEST | ["NOT_BLANK", "SIZE"]
        "new@example.com"   | "NE"          | "Password_1234" | HttpStatus.BAD_REQUEST | ["SIZE"]
        "new@example.com"   | "N" * 51      | "Password_1234" | HttpStatus.BAD_REQUEST | ["SIZE"]
        "new@example.com"   | "New/invalid" | "Password_1234" | HttpStatus.BAD_REQUEST | ["PATTERN"]
        "new@example.com"   | "Sonia"       | "Password_1234" | HttpStatus.BAD_REQUEST | ["UNIQUE_NICKNAME"]
        "new@example.com"   | "New"         | ""              | HttpStatus.BAD_REQUEST | ["NOT_EMPTY", "PATTERN", "SIZE"]
        "new@example.com"   | "New"         | "Pa_1234"       | HttpStatus.BAD_REQUEST | ["SIZE"]
        "new@example.com"   | "New"         | "Password12345" | HttpStatus.BAD_REQUEST | ["PATTERN"]
        "new@example.com"   | "New"         | "Password_____" | HttpStatus.BAD_REQUEST | ["PATTERN"]
        "new@example.com"   | "New"         | "password_1234" | HttpStatus.BAD_REQUEST | ["PATTERN"]
        "new@example.com"   | "New"         | "PASSWORD_1234" | HttpStatus.BAD_REQUEST | ["PATTERN"]
    }

    static MockHttpServletRequestBuilder registerRequest(email = "new@example.com", nickname = "New",
                                                         password = "Password_1234") {
        var data = [
                "email"   : email,
                "nickname": nickname,
                "password": password
        ]
        post(endpointUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapperConfig.getInstance().writeValueAsString(data))
    }
}
