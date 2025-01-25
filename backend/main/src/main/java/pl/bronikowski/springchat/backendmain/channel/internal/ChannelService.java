package pl.bronikowski.springchat.backendmain.channel.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;
import pl.bronikowski.springchat.backendmain.channel.api.dto.member.AddChannelMemberRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.ChannelBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.ChannelDetailsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.CreateChannelRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.member.KickChannelMemberRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.UpdateChannelMemberRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.UpdateChannelRequest;
import pl.bronikowski.springchat.backendmain.channel.api.exception.ChannelOperationNotEnoughRightsException;
import pl.bronikowski.springchat.backendmain.exception.AppNotFoundException;
import pl.bronikowski.springchat.backendmain.member.internal.MemberRepository;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final MemberRepository memberRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public ChannelDetailsDto getById(UUID id) {
        return channelRepository.findWithMembersById(id)
                .map(channelMapper::mapToChannelDetailsDto)
                .orElseThrow(AppNotFoundException::new);
    }

    @Transactional
    public ChannelBasicsDto create(CreateChannelRequest request, String authResourceId) {
        var member = memberRepository.findByAuthResourceId(authResourceId)
                .orElseThrow(AppNotFoundException::new);
        var channel = new Channel(request, member);
        channelRepository.save(channel);
        return channelMapper.mapToChannelBasicsDto(channel);
    }

    @Transactional
    public void update(UUID id, UpdateChannelRequest request) {
        var channel = channelRepository.findWithMembersById(id)
                .orElseThrow(AppNotFoundException::new);
        channel.update(request);
    }

    @Transactional
    public void addMember(UUID id, AddChannelMemberRequest request, String authResourceId) {
        var channel = channelRepository.findWithMembersById(id)
                .orElseThrow(AppNotFoundException::new);
        var member = channel.findMemberByUserAuthResourceId(authResourceId)
                .orElseThrow(AppNotFoundException::new);
        var rights = member.getRights().contains(ChannelMemberRight.MANAGE) && request.rights() != null
                ? request.rights()
                : ChannelMemberRight.DEFAULT_RIGHTS;
        var newMember = memberRepository.getReferenceById(request.member().id());
        channel.findMemberById(newMember.getId())
                .ifPresentOrElse(newChannelMember -> newChannelMember.restore(rights),
                        () -> channel.addMember(newMember, rights));
    }

    @Transactional
    public void updateMember(UUID id, UpdateChannelMemberRequest request) {
        var channel = channelRepository.findWithMembersById(id)
                .orElseThrow(AppNotFoundException::new);
        var updatedMember = channel.findMemberById(request.memberId())
                .orElseThrow(AppNotFoundException::new);
        updatedMember.update(request);
    }

    @Transactional
    public void leave(UUID id, String authResourceId) {
        var channel = channelRepository.findWithMembersById(id)
                .orElseThrow(AppNotFoundException::new);
        channel.findMemberByUserAuthResourceId(authResourceId)
                .ifPresent(channelMember -> channelMember.delete(clock));
    }

    @Transactional
    public void kickMember(UUID id, KickChannelMemberRequest request) {
        var channel = channelRepository.findWithMembersById(id)
                .orElseThrow(AppNotFoundException::new);
        var kickedMember = channel.findMemberById(request.memberId())
                .orElseThrow(AppNotFoundException::new);
        kickedMember.delete(clock);
        /* todo: kill subscription and kick from video room - plugin was modified to include id of user in allowed value
         */
    }

    @Transactional
    public void delete(UUID id) {
        var channel = channelRepository.findWithMembersById(id)
                .orElseThrow(AppNotFoundException::new);
        channel.delete(clock);
    }
}
