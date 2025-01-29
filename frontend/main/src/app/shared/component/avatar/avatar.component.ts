import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  HostBinding,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { AvatarModule } from 'primeng/avatar';
import { RippleModule } from 'primeng/ripple';
import { ColorService } from '../../service/color.service';
import { SkeletonModule } from 'primeng/skeleton';
import { RouterModule, UrlTree } from '@angular/router';

@Component({
  selector: 'app-avatar',
  imports: [
    CommonModule,
    AvatarModule,
    RippleModule,
    SkeletonModule,
    RouterModule,
  ],
  templateUrl: './avatar.component.html',
  styleUrl: './avatar.component.scss',
})
export class AvatarComponent implements OnInit {
  @Input() public name?: string;
  @Input() public id?: string;
  @Input() public routerLink?: string | any[] | UrlTree;
  @Input() public size: 'normal' | 'large' | 'xlarge' = 'large';
  @Output() public avatarClick = new EventEmitter<MouseEvent>();
  @HostBinding('style.--app-avatar-color') defaultColor = '';
  @HostBinding('style.--app-avatar-hover-color') hoverColor = '';
  private colorService = inject(ColorService);

  public ngOnInit(): void {
    if (this.id !== undefined) {
      this.hoverColor = this.colorService.getLightColorVarFormUUID(this.id);
      this.defaultColor = this.colorService.getColorVarFromUUID(this.id);
    }
  }

  protected isInteractive(): boolean {
    return this.routerLink !== undefined || this.avatarClick.observed;
  }

  protected getSkeletonSize(): string {
    switch (this.size) {
      case 'normal':
        return '2rem';
      case 'large':
        return '3rem';
      case 'xlarge':
        return '4rem';
    }
  }
}
