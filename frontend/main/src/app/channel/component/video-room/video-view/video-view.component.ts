import { Component, ElementRef, Input, ViewChild } from '@angular/core';

@Component({
  selector: 'app-video-view',
  imports: [],
  templateUrl: './video-view.component.html',
  styleUrl: './video-view.component.scss',
})
export class VideoViewComponent {
  @Input({ required: true }) mediaStream!: MediaStream;
  @ViewChild('video')
  protected set videoComponent(element: ElementRef<HTMLVideoElement>) {
    if (element === undefined) {
      return;
    }
    element.nativeElement.srcObject = this.mediaStream;
  }
}
