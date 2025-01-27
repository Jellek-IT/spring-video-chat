import { Pageable } from '../../../shared/model/table-filter.model';
import { MemberChannelMessageQueryParams } from './member-channel-message-query-params.model';

export interface MemberChannelMessageQueryParamsPageable
  extends MemberChannelMessageQueryParams,
    Pageable {}
