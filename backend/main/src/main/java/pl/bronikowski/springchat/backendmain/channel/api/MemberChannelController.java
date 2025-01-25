package pl.bronikowski.springchat.backendmain.channel.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bronikowski.springchat.backendmain.authserver.api.UserContextProvider;
import pl.bronikowski.springchat.backendmain.channel.api.dto.ChannelBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.ChannelDetailsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.CreateChannelRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.MemberChannelQueryParams;
import pl.bronikowski.springchat.backendmain.channel.api.dto.UpdateChannelMemberRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.UpdateChannelRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.member.AddChannelMemberRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.member.KickChannelMemberRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.ChannelMessageBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.MemberChannelMessageQueryParams;
import pl.bronikowski.springchat.backendmain.channel.api.validation.ExistingMemberChannelWithRights;
import pl.bronikowski.springchat.backendmain.channel.internal.ChannelSearchService;
import pl.bronikowski.springchat.backendmain.channel.internal.ChannelService;
import pl.bronikowski.springchat.backendmain.channel.internal.message.ChannelMessageSearchService;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/member/channels")
@RequiredArgsConstructor
@Tag(name = "[MEMBER] Channels management", description = "member-channel-controller")
public class MemberChannelController {
    private final ChannelSearchService channelSearchService;
    private final ChannelMessageSearchService channelMessageSearchService;
    private final ChannelService channelService;

    @GetMapping
    public Page<ChannelBasicsDto> getAll(@Valid MemberChannelQueryParams queryParams, Pageable pageable) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        return channelSearchService.getAllByMemberQueryParams(queryParams, pageable, authResourceId);
    }

    @GetMapping("/{id}")
    public ChannelDetailsDto getById(@PathVariable @ExistingMemberChannelWithRights UUID id) {
        return channelService.getById(id);
    }

    @GetMapping("/{id}/messages")
    public Page<ChannelMessageBasicsDto> getAllMessagesById(
            @PathVariable @ExistingMemberChannelWithRights(ChannelMemberRight.READ) UUID id,
            MemberChannelMessageQueryParams queryParams,
            Pageable pageable) {
        return channelMessageSearchService.getAllByMemberQueryParams(id, queryParams, pageable);
    }

    @PostMapping
    public ChannelBasicsDto create(@Valid @RequestBody CreateChannelRequest request) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        return channelService.create(request, authResourceId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @ExistingMemberChannelWithRights(ChannelMemberRight.MANAGE) UUID id,
                       @Valid @RequestBody UpdateChannelRequest request) {
        channelService.update(id, request);
    }

    @PostMapping("/{id}/add-member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMember(@PathVariable @ExistingMemberChannelWithRights(ChannelMemberRight.INVITE) UUID id,
                          @Valid @RequestBody AddChannelMemberRequest request) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        channelService.addMember(id, request, authResourceId);
    }

    @PostMapping("/{id}/update-member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMember(@PathVariable @ExistingMemberChannelWithRights(ChannelMemberRight.MANAGE) UUID id,
                             @Valid @RequestBody UpdateChannelMemberRequest request) {
        channelService.updateMember(id, request);
    }

    @PostMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(@PathVariable @ExistingMemberChannelWithRights({}) UUID id) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        channelService.leave(id, authResourceId);
    }

    @PostMapping("/{id}/kick-member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void kickMember(@PathVariable @ExistingMemberChannelWithRights(ChannelMemberRight.KICK) UUID id,
                           KickChannelMemberRequest request) {
        channelService.kickMember(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @ExistingMemberChannelWithRights(ChannelMemberRight.MANAGE) UUID id) {
        channelService.delete(id);
    }
}
