import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Output,
  ViewChild,
} from '@angular/core';
import mathUtils from '../../utils/math-utils';

interface DragDetails {
  startX: number;
  startScroll: number;
  pastDelta: boolean;
}

export interface DraggableScrollDragEvent {
  dragging: boolean;
}

@Component({
  selector: 'app-draggable-scroll',
  imports: [],
  templateUrl: './draggable-scroll.component.html',
  styleUrl: './draggable-scroll.component.scss',
})
export class DraggableScrollComponent {
  protected readonly dragDelta = 10;

  @Output()
  public drag = new EventEmitter<DraggableScrollDragEvent>();

  @ViewChild('container')
  protected containerComponent?: ElementRef<HTMLElement>;

  private dragDetails: DragDetails | null = null;

  protected handleDragStart(event: PointerEvent) {
    if (this.containerComponent === undefined) {
      return;
    }
    this.dragDetails = {
      startX: event.clientX,
      startScroll: this.containerComponent.nativeElement.scrollLeft,
      pastDelta: false,
    };
  }

  @HostListener('document:pointerup')
  @HostListener('document:pointercancel')
  protected handlePointerRelease(): void {
    // next pass, so that click events can fire first
    setTimeout(() => {
      this.dragDetails = null;
      this.drag.emit({ dragging: false });
    });
  }

  @HostListener('document:pointermove', ['$event'])
  protected handleContentDrag(event: PointerEvent): void {
    if (this.dragDetails === null || this.containerComponent === undefined) {
      return;
    }
    const element = this.containerComponent.nativeElement;
    const delta = this.dragDetails.startX - event.clientX;
    if (!this.dragDetails.pastDelta && Math.abs(delta) > this.dragDelta) {
      this.dragDetails.pastDelta = true;
      this.drag.emit({ dragging: true });
    }

    const result = mathUtils.clamp(
      delta + this.dragDetails.startScroll,
      0,
      element.scrollWidth
    );
    this.containerComponent.nativeElement.scrollTo(result, 0);
  }
}
