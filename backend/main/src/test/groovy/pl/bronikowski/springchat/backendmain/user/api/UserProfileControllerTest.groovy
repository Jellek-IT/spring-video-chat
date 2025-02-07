package pl.bronikowski.springchat.backendmain.user.api

import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import pl.bronikowski.springchat.backendmain.BaseSpecification
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles
import pl.bronikowski.springchat.backendmain.exception.ExceptionResponse
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberProfileDto
import pl.bronikowski.springchat.backendmain.shared.utils.FileUtils

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart

@Sql("classpath:sql/Member.sql")
@Sql(value = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserProfileControllerTest extends BaseSpecification {
    static final endpointUrl = "/users/profile"
    static final USER_WITH_PROFILE_PICTURE_ID = "00000000-0000-0000-0000-000000000001"

    @WithMockUser(roles = Roles.MEMBER, value = USER_WITH_PROFILE_PICTURE_ID)
    def "should return user profile details"() {
        when: "request is sent"
        var response = mvc.perform(getUserProfileRequest())
                .andReturn()
                .response

        then: "response status is 200 ok"
        response.status == HttpStatus.OK.value()

        and: "response data is valid"
        var memberDto = objectMapper.readValue(response.contentAsString, MemberProfileDto)
        memberDto.id == UUID.fromString("00000000-0000-0000-0000-000000000001")
        memberDto.nickname == "Sonia"
        memberDto.email == "sonia@example.com"
        memberDto.type == UserType.MEMBER
        memberDto.emailVerified
        memberDto.hasProfilePicture
    }

    @WithMockUser(roles = Roles.MEMBER, value = USER_WITH_PROFILE_PICTURE_ID)
    def "should upload profile picture"() {
        when: "request is sent"
        var response = mvc.perform(updateProfilePictureRequest())
                .andReturn()
                .response

        then: "response status is 204 no content"
        response.status == HttpStatus.NO_CONTENT.value()

        and: "old profile picture was deleted"
        1 * s3Client.deleteObject(_)

        and: "new profile picture was uploaded"
        1 * s3Client.putObject(_, _)

        and: "changes were persisted in db"
        var member = memberRepository.findById(UUID.fromString(USER_WITH_PROFILE_PICTURE_ID)).orElseThrow()
        member.profilePicture.id != null
        var storageFile = storageFileRepository.findById(member.profilePicture.id).orElseThrow()
        storageFile.createdAt == clock.instant()
        storageFile.folder == "user/00000000-0000-0000-0000-000000000001"
        storageFile.name == "profile-picture.jpeg"
    }

    @WithMockUser(roles = Roles.MEMBER, value = USER_WITH_PROFILE_PICTURE_ID)
    def "should delete profile picture"() {
        when: "request is sent"
        var response = mvc.perform(updateProfilePictureRequest(null))
                .andReturn()
                .response

        then: "response status is 204 no content"
        response.status == HttpStatus.NO_CONTENT.value()

        and: "old profile picture was deleted"
        1 * s3Client.deleteObject(_)

        and: "new profile picture was not uploaded"
        0 * s3Client.putObject(_, _)

        and: "changes were persisted in db"
        var member = memberRepository.findById(UUID.fromString(USER_WITH_PROFILE_PICTURE_ID)).orElseThrow()
        member.profilePicture == null
    }

    @WithMockUser(roles = Roles.MEMBER)
    def "should not allow to upload profile picture with invalid format"() {
        when: "request is sent"
        var response = mvc.perform(updateProfilePictureRequest("/sample/image.gif"))
                .andReturn()
                .response

        then: "response status is 400 bad request"
        response.status == HttpStatus.BAD_REQUEST.value()

        and: "valid error types are present"
        def exceptionResponse = objectMapper.readValue(response.contentAsString, ExceptionResponse)
        exceptionResponse.types.collect { it.type() }.sort() == ["ALLOWED_MEDIA_TYPE"]
    }

    @WithMockUser(roles = Roles.MEMBER, value = USER_WITH_PROFILE_PICTURE_ID)
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
        request << [getUserProfileRequest(),
                    updateProfilePictureRequest(),
                    getProfilePictureRequest()]
    }

    static MockHttpServletRequestBuilder getUserProfileRequest() {
        get(endpointUrl)
    }

    MockHttpServletRequestBuilder updateProfilePictureRequest(String resourceLocation = "/sample/image.jpeg") {
        if (resourceLocation != null) {
            var resource = getClass().getResource(resourceLocation)
            var imageStream = resource.newInputStream()
            var contentType = FileUtils.getMediaType(imageStream, resource.getFile()).toString()
            var multipartFile = new MockMultipartFile("file", resource.getFile(), contentType, imageStream)
            multipart("${endpointUrl}/profile-picture")
                    .file(multipartFile)
        } else {
            multipart("${endpointUrl}/profile-picture")
        }
    }

    static MockHttpServletRequestBuilder getProfilePictureRequest() {
        get("${endpointUrl}/profile-picture")
    }
}
