import { Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { RouterModule } from '@angular/router';
import { MenuModule } from 'primeng/menu';
import { MemberProfileDto } from '../../../user/model/member-profile-dto.model';
import { CurrentUserService } from '../../../user/service/current-user.service';
import { MenuItem } from 'primeng/api';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../../../environments/environment';
import { MenubarModule } from 'primeng/menubar';
import { DialogService } from 'primeng/dynamicdialog';
import {
  AddChannelDialogComponent,
  AddChannelDialogConfig,
} from '../../../channel/component/add-channel-dialog/add-channel-dialog.component';
import { AvatarComponent } from '../../component/avatar/avatar.component';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import {
  ChannelsStripComponent,
  ExpandEvent,
} from '../../../channel/component/channels-strip/channels-strip.component';
import { ContainerComponent } from '../../component/container/container.component';
import { LoaderComponent } from '../../component/loader/loader.component';
import { TypographyComponent } from '../../component/typography/typography.component';
import { Subscription } from 'rxjs';
import { StompService } from '../../../core/stomp/service/stomp.service';
import { StompConnectionState } from '../../../core/stomp/enum/internal/stomp-state.enum';

@Component({
  selector: 'app-user-layout',
  imports: [
    CommonModule,
    CardModule,
    RouterModule,
    MenuModule,
    MenubarModule,
    AvatarComponent,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    TranslateModule,
    FormsModule,
    ChannelsStripComponent,
    ContainerComponent,
    LoaderComponent,
    TypographyComponent,
  ],
  templateUrl: './user-layout.component.html',
  styleUrl: './user-layout.component.scss',
})
export class UserLayoutComponent implements OnInit, OnDestroy {
  private readonly currentUserService = inject(CurrentUserService);
  private readonly translateService = inject(TranslateService);
  private readonly keycloak = inject(Keycloak);
  private readonly dialogService = inject(DialogService);
  private readonly stompService = inject(StompService);

  private userProfileSubscription!: Subscription;
  @ViewChild('channelsStrip')
  protected channelsStripComponent?: ChannelsStripComponent;
  protected userProfile!: MemberProfileDto;
  protected userMenuItems!: MenuItem[];
  protected menuItems: MenuItem[] | undefined;
  protected expandChannels = false;
  protected connected = false;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => (this.userProfile = userProfile));
    this.initUserMenuItems();
    this.initMenuItems();
    this.stompService.getConnectionStateAsObservable().subscribe((state) => {
      if (state === StompConnectionState.ACTIVATED) {
        this.connected = true;
      } else {
        this.connected = false;
      }
    });
    this.stompService.connect();
  }

  public ngOnDestroy(): void {
    this.stompService.disconnect();
    this.userProfileSubscription.unsubscribe();
  }

  private initUserMenuItems(): void {
    this.userMenuItems = [
      {
        label: this.translateService.instant('menu.logout'),
        command: () => {
          this.keycloak.logout({ redirectUri: environment.url });
        },
      },
    ];
  }

  private initMenuItems(): void {
    this.menuItems = [
      {
        label: this.translateService.instant('menu.addChannel'),
        icon: 'pi pi-plus',
        command: () => this.openAddChannelPopup(),
      },
    ];
  }

  private openAddChannelPopup(): void {
    const data: AddChannelDialogConfig = {
      onCreateEnd: () => this.channelsStripComponent?.reloadChannels(),
    };
    this.dialogService.open(AddChannelDialogComponent, {
      closable: true,
      modal: true,
      header: this.translateService.instant('channel.addTitle'),
      dismissableMask: true,
      data,
    });
  }

  protected handleChannelExpandStateChange({ expanded }: ExpandEvent): void {
    this.expandChannels = expanded;
  }
}
