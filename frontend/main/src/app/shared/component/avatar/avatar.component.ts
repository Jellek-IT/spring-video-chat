import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  EventEmitter,
  HostBinding,
  inject,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { AvatarModule } from 'primeng/avatar';
import { RippleModule } from 'primeng/ripple';
import { ColorService } from '../../service/color.service';
import { SkeletonModule } from 'primeng/skeleton';
import { RouterModule, UrlTree } from '@angular/router';
import { TooltipModule } from '../../directive/primeng/tooltip.primeng.spec';

@Component({
  selector: 'app-avatar',
  imports: [
    CommonModule,
    AvatarModule,
    RippleModule,
    SkeletonModule,
    RouterModule,
    TooltipModule,
  ],
  templateUrl: './avatar.component.html',
  styleUrl: './avatar.component.scss',
})
export class AvatarComponent implements OnChanges {
  @Input() public name?: string | null;
  @Input() public id?: string | null;
  @Input() public routerLink?: string | any[] | UrlTree;
  @Input() public size: 'normal' | 'large' | 'xlarge' = 'large';
  @Input() public iconUrl?: string | null;
  @Input() public appendToBody: boolean = true;
  @Input() public tooltipPosition: 'right' | 'left' | 'top' | 'bottom' =
    'bottom';
  @Output() public avatarClick = new EventEmitter<MouseEvent>();
  @HostBinding('style.--app-avatar-color')
  protected defaultColor = '';
  @HostBinding('style.--app-avatar-hover-color')
  protected hoverColor = '';
  private colorService = inject(ColorService);

  public ngOnChanges(changes: SimpleChanges): void {
    if (changes['id'] !== undefined) {
      if (!this.isSkeleton()) {
        this.hoverColor = this.colorService.getLightColorVarFormUUID(this.id!);
        this.defaultColor = this.colorService.getColorVarFromUUID(this.id!);
      }
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

  protected isSkeleton() {
    return (
      this.name === undefined ||
      this.name === null ||
      this.id === undefined ||
      this.id === null
    );
  }

  protected getLabel(): string | undefined {
    return this.iconUrl !== null && this.iconUrl !== undefined
      ? undefined
      : this.name?.at(0);
  }
}
