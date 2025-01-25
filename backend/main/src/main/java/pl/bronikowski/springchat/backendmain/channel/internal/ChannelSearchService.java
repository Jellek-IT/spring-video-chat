package pl.bronikowski.springchat.backendmain.channel.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bronikowski.springchat.backendmain.channel.api.dto.ChannelBasicsDto;
import pl.bronikowski.springchat.backendmain.channel.api.dto.MemberChannelQueryParams;
import pl.bronikowski.springchat.backendmain.channel.internal.specification.memberqueryparams.MemberChannelQueryParamsSpecification;
import pl.bronikowski.springchat.backendmain.shared.OffsetPageRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChannelSearchService {
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;

    public Page<ChannelBasicsDto> getAllByMemberQueryParams(MemberChannelQueryParams queryParams, Pageable pageable,
                                                            String authResourceId) {
        var offset = queryParams.offset() != null ? queryParams.offset() : 0;
        var offsetPageable = OffsetPageRequest.of(pageable, offset);
        var specification = new MemberChannelQueryParamsSpecification(queryParams, authResourceId);
        return channelRepository.findAll(specification, offsetPageable)
                .map(channelMapper::mapToChannelBasicsDto);
    }
}
