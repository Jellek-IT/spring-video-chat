import { Pageable } from '../../shared/model/table-filter.model';
import { MemberChannelQueryParams } from './member-channel-query-params.model';

export interface MemberChannelQueryParamsPageable
  extends MemberChannelQueryParams,
    Pageable {}
