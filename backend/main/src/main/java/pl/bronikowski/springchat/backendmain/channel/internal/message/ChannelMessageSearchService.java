package pl.bronikowski.springchat.backendmain.channel.internal.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.ChannelMessageBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.MemberChannelMessageQueryParams;
import pl.bronikowski.springchat.backendmain.channel.internal.message.specification.memberqueryparams.MemberChannelMessageQueryParamsSpecification;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChannelMessageSearchService {
    private final ChannelMessageRepository channelMessageRepository;
    private final ChannelMessageMapper channelMessageMapper;

    public Page<ChannelMessageBasicsDto> getAllByMemberQueryParams(UUID channelId,
                                                                   MemberChannelMessageQueryParams queryParams,
                                                                   Pageable pageable) {
        var specification = new MemberChannelMessageQueryParamsSpecification(queryParams, channelId);
        return channelMessageRepository.findAll(specification, pageable, EntityGraph.EntityGraphType.LOAD,
                        ChannelMessage_.GRAPH_CHANNEL_MESSAGE_WITH_MEMBER)
                .map(channelMessageMapper::mapToMessageBasicsDto);
    }
}
