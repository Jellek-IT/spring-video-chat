import { CommonModule } from '@angular/common';
import {
  AfterViewChecked,
  Component,
  ElementRef,
  EventEmitter,
  inject,
  Output,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { CardModule } from 'primeng/card';
import { AvatarComponent } from '../../../shared/component/avatar/avatar.component';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';
import { ChannelBasicsDto } from '../../model/channel-basics-dto.model';
import { MemberChannelQueryParamsPageable } from '../../model/member-channel-query-params-pageable.model';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { LoaderComponent } from '../../../shared/component/loader/loader.component';
import { MemberChannelService } from '../../service/api/member-channel.service';

interface ChannelData {
  data: ChannelBasicsDto | null;
  loaded: boolean;
}

interface IndexRange {
  start: number;
  length: number;
}

interface ElementRefWithIndex {
  elementRef: ElementRef<HTMLElement>;
  index: number;
}

export interface ExpandEvent {
  expanded: boolean;
}

@Component({
  selector: 'app-channels-strip',
  imports: [
    CommonModule,
    CardModule,
    AvatarComponent,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    TranslateModule,
    FormsModule,
    TypographyComponent,
    LoaderComponent,
  ],
  templateUrl: './channels-strip.component.html',
  styleUrl: './channels-strip.component.scss',
})
export class ChannelsStripComponent implements AfterViewChecked {
  private readonly firstChannelsBatchSize = 1;
  private readonly channelsSearchNameDebounceTime = 300;
  private readonly channelsScrollLoadDebounceTime = 300;
  private readonly channelsSort = 'name,asc';
  private readonly memberChannelService = inject(MemberChannelService);

  @Output() public expandStateChange = new EventEmitter<ExpandEvent>();

  private _channelsNameSearch = '';
  private channelsNameSearchDebounceTimeout: ReturnType<
    typeof setTimeout
  > | null = null;
  private channelsScrollLoadDebounceTimeout: ReturnType<
    typeof setTimeout
  > | null = null;
  private _channelsScrollableContainerRef?: ElementRef<HTMLElement>;

  private shouldFirstCheckVisibleChannels = false;
  private channelSearchObservable: Subscription | null = null;
  private loadVisibleChannelsObservables: Subscription[] = [];
  @ViewChildren('channelElements')
  protected channelElementsRef?: QueryList<ElementRef<HTMLElement>>;
  @ViewChild('channelsScrollableContainer')
  protected set channelsScrollableContainerRef(
    elementRef: ElementRef<HTMLElement> | undefined
  ) {
    if (elementRef === undefined) {
      return;
    }
    this._channelsScrollableContainerRef = elementRef;
  }
  protected channelsLoading = false;
  protected channels: ChannelData[] = [];

  protected set channelNameSearch(value: string) {
    this._channelsNameSearch = value;
    if (this.channelsNameSearchDebounceTimeout !== null) {
      clearTimeout(this.channelsNameSearchDebounceTimeout);
    }
    this.channelsNameSearchDebounceTimeout = setTimeout(() => {
      this.channelsNameSearchDebounceTimeout = null;
      this.reloadChannels();
    }, this.channelsSearchNameDebounceTime);
  }

  protected get channelNameSearch(): string {
    return this._channelsNameSearch;
  }

  public ngOnInit(): void {
    this.reloadChannels();
  }
  public ngAfterViewChecked(): void {
    if (this.shouldFirstCheckVisibleChannels) {
      this.shouldFirstCheckVisibleChannels = false;
      this.loadVisibleChannels();
    }
  }

  public reloadChannels(): void {
    this.shouldFirstCheckVisibleChannels = false;
    this.loadVisibleChannelsObservables.forEach((subscription) =>
      subscription.unsubscribe()
    );
    this.loadVisibleChannelsObservables = [];
    this.channelsLoading = true;
    const filter: MemberChannelQueryParamsPageable = {
      size: this.firstChannelsBatchSize,
      page: 0,
      sort: this.channelsSort,
      name: this._channelsNameSearch,
    };
    this.channelSearchObservable && this.channelSearchObservable.unsubscribe();
    this.channelSearchObservable = this.memberChannelService
      .getAll(filter)
      .subscribe((res) => {
        const channels: ChannelData[] = res.content.map((el) => ({
          data: el,
          loaded: true,
        }));
        const unloadedChannels: ChannelData[] = new Array(
          res.totalElements - channels.length
        )
          .fill(null)
          .map((_) => ({ data: null, loaded: false }));
        this.channels = channels.concat(unloadedChannels);
        this.channelsLoading = false;
        this.shouldFirstCheckVisibleChannels = true;
      });
  }

  protected loadVisibleChannels(): void {
    if (
      this.channelElementsRef === undefined ||
      this._channelsScrollableContainerRef === undefined ||
      this.channels.length === 0 ||
      this.channelElementsRef.length === 0
    ) {
      return;
    }
    const containerRect =
      this._channelsScrollableContainerRef.nativeElement.getBoundingClientRect();
    const containerTop = containerRect.y;
    const containerBottom = containerTop + containerRect.height;
    const notLoadedVisibleElements: ElementRefWithIndex[] =
      this.channelElementsRef
        .map((elementRef, index) => ({ index, elementRef }))
        .filter(
          (data) =>
            this.channels[data.index] !== undefined &&
            !this.channels[data.index].loaded
        )
        .filter((element) => {
          const rect = element.elementRef.nativeElement.getBoundingClientRect();
          const bottom = rect.x + rect.y;
          return bottom >= containerTop && rect.y <= containerBottom;
        });
    if (notLoadedVisibleElements.length === 0) {
      return;
    }
    notLoadedVisibleElements.forEach(
      (element) => (this.channels[element.index].loaded = true)
    );
    const notLoadedVisibleElementsRanges = notLoadedVisibleElements.reduce(
      (ranges: IndexRange[], num, index, array) => {
        if (index === 0 || num.index !== array[index - 1].index + 1) {
          ranges.push({ start: num.index, length: 1 });
        } else {
          ranges[ranges.length - 1].length++;
        }
        return ranges;
      },
      [] as IndexRange[]
    );
    notLoadedVisibleElementsRanges.forEach((range) => {
      const filter: MemberChannelQueryParamsPageable = {
        size: range.length,
        page: 0,
        sort: this.channelsSort,
        name: this._channelsNameSearch,
        offset: range.start,
      };
      const subscription = this.memberChannelService
        .getAll(filter)
        .subscribe((res) => {
          const newChannels = [...this.channels];
          for (let i = 0; i < res.content.length; i++) {
            newChannels[i + range.start].data = res.content[i];
          }
          this.channels = newChannels;
        });
      this.loadVisibleChannelsObservables.push(subscription);
    });
  }

  protected handleChannelSearchFocus(): void {
    this.expandStateChange.emit({ expanded: true });
  }

  protected handleChannelSearchFucusOut(): void {
    this.expandStateChange.emit({ expanded: false });
  }

  protected onChannelsContainerScroll(): void {
    if (this.channelsScrollLoadDebounceTimeout !== null) {
      clearTimeout(this.channelsScrollLoadDebounceTimeout);
    }
    this.channelsScrollLoadDebounceTimeout = setTimeout(() => {
      this.channelsScrollLoadDebounceTimeout = null;
      this.loadVisibleChannels();
    }, this.channelsScrollLoadDebounceTime);
  }
}
