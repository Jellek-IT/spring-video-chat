import { Pipe, PipeTransform } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { ChannelMemberRights } from '../enum/channel-member-rights.enum';

@Pipe({
  name: 'channelMemberRight',
  pure: false,
  standalone: true,
})
export class ChannelMemberRightPipe
  extends TranslatePipe
  implements PipeTransform
{
  public override transform(value: ChannelMemberRights): string {
    return value !== undefined && value !== null
      ? super.transform(`channel.enum.right.${value}`)
      : super.transform(`common.unknown`);
  }
}
