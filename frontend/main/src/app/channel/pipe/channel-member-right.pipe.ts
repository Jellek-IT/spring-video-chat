import { Pipe, PipeTransform } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { ChannelMemberRight } from '../enum/channel-member-right.enum';

@Pipe({
  name: 'channelMemberRight',
  pure: false,
  standalone: true,
})
export class ChannelMemberRightPipe
  extends TranslatePipe
  implements PipeTransform
{
  public override transform(value: ChannelMemberRight): string {
    return value !== undefined && value !== null
      ? super.transform(`channel.enum.right.${value}`)
      : super.transform(`common.unknown`);
  }
}
