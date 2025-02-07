package pl.bronikowski.springchat.backendmain.member.api

import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import pl.bronikowski.springchat.backendmain.BaseSpecification
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Sql("classpath:sql/Member.sql")
@Sql(value = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MemberControllerTest extends BaseSpecification {
    static final endpointUrl = "/members"
    static final USER_WITH_PROFILE_PICTURE_ID = "00000000-0000-0000-0000-000000000001"

    @WithMockUser(roles = Roles.MEMBER)
    def "should download profile picture"() {
        given: "s3Client get object response"
        mockS3ClientGetObjectResponse()

        when: "request is sent"
        var response = mvc.perform(getProfilePictureRequest())
                .andReturn()
                .response

        then: "response status is 200 ok"
        response.status == HttpStatus.OK.value()

        and: "response data is valid"
        response.contentAsString == "response"
        response.contentType == "image/jpeg"
        response.contentLength == 8
    }

    def "should not allow access for unauthorized users"() {
        when: "request is sent"
        var response = mvc.perform(request)
                .andReturn()
                .response

        then: "response status is 401 unauthorized"
        response.status == HttpStatus.UNAUTHORIZED.value()

        where:
        request << [getProfilePictureRequest()]
    }

    static MockHttpServletRequestBuilder getProfilePictureRequest(id = USER_WITH_PROFILE_PICTURE_ID) {
        get("${endpointUrl}/${id}/profile-picture")
    }
}
