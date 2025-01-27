import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'app-typography',
  imports: [CommonModule, SkeletonModule],
  templateUrl: './typography.component.html',
  styleUrl: './typography.component.scss',
})
export class TypographyComponent {
  @Input({ required: true }) public type!: 'title' | 'subtitle' | 'subtitle2';
  @Input() public skeleton = false;
}
