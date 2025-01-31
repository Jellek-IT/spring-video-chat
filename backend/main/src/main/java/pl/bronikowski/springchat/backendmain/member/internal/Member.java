package pl.bronikowski.springchat.backendmain.member.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.bronikowski.springchat.backendmain.member.api.dto.RegisterMemberRequest;
import pl.bronikowski.springchat.backendmain.user.api.UserType;
import pl.bronikowski.springchat.backendmain.user.internal.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedEntityGraph(name = "Member.withProfilePicture",
        attributeNodes = {@NamedAttributeNode("profilePicture")})
public class Member extends User {
    @Column(nullable = false)
    private String nickname;

    public Member(RegisterMemberRequest request) {
        super(UserType.MEMBER, request.email(), true);
        this.nickname = request.nickname();
    }

    public Boolean getHasProfilePicture() {
        return this.getProfilePicture() != null;
    }
}
