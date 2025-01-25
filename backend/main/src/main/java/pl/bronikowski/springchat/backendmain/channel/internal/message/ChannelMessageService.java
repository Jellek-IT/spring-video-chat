package pl.bronikowski.springchat.backendmain.channel.internal.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.ChannelMessageBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.CreateChannelMessagePayload;
import pl.bronikowski.springchat.backendmain.channel.internal.ChannelRepository;
import pl.bronikowski.springchat.backendmain.exception.AppNotFoundException;
import pl.bronikowski.springchat.backendmain.member.internal.MemberRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelMessageService {
    private final ChannelMessageRepository channelMessageRepository;
    private final ChannelMessageMapper channelMessageMapper;
    private final MemberRepository memberRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public ChannelMessageBasicsDto create(UUID channelId, CreateChannelMessagePayload payload, String authResourceId) {
        var member = memberRepository.findByAuthResourceId(authResourceId)
                .orElseThrow(AppNotFoundException::new);
        var channel = channelRepository.getReferenceById(channelId);
        var message = new ChannelMessage(payload, channel, member);
        channelMessageRepository.save(message);
        return channelMessageMapper.mapToMessageBasicsDto(message);
    }
}
