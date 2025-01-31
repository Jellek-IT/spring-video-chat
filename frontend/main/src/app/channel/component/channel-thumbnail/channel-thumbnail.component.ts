import {
  AfterViewChecked,
  Component,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { AvatarComponent } from '../../../shared/component/avatar/avatar.component';
import { ChannelBasicsDto } from '../../model/channel-basics-dto.model';
import { UrlTree } from '@angular/router';
import { MemberChannelService } from '../../service/api/member-channel.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-channel-thumbnail',
  imports: [AvatarComponent],
  templateUrl: './channel-thumbnail.component.html',
  styleUrl: './channel-thumbnail.component.scss',
})
export class ChannelThumbnailComponent implements OnChanges, OnDestroy {
  @Input() channel?: ChannelBasicsDto | null;
  @Output() public avatarClick = new EventEmitter<MouseEvent>();
  @Input() public routerLink?: string | any[] | UrlTree;
  @Input() public size: 'normal' | 'large' | 'xlarge' = 'large';
  protected getThumbnailSubscription?: Subscription;

  private readonly memberChannelService = inject(MemberChannelService);
  protected thumbnailUrl: string | null = null;

  public ngOnChanges(changes: SimpleChanges): void {
    if (changes['channel'] !== undefined) {
      this.loadThumbnail();
    }
  }
  public ngOnDestroy(): void {
    this.getThumbnailSubscription?.unsubscribe();
  }

  private loadThumbnail(): void {
    this.getThumbnailSubscription?.unsubscribe();
    if (
      this.channel === undefined ||
      this.channel === null ||
      !this.channel.hasThumbnail
    ) {
      this.thumbnailUrl = null;
      return;
    }
    this.getThumbnailSubscription = this.memberChannelService
      .getThumbnail(this.channel.id)
      .subscribe((res) => {
        const reader = new FileReader();
        reader.onload = (): void => {
          this.thumbnailUrl = reader.result as string;
        };
        reader.readAsDataURL(res);
      });
  }
}
