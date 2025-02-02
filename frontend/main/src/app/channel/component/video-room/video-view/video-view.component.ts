import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  Input,
  ViewChild,
} from '@angular/core';

@Component({
  selector: 'app-video-view',
  imports: [CommonModule],
  templateUrl: './video-view.component.html',
  styleUrl: './video-view.component.scss',
})
export class VideoViewComponent implements AfterViewInit {
  @Input({ required: true }) mediaStream!: MediaStream;
  @ViewChild('videoContainer')
  protected videoContainerComponent?: ElementRef<HTMLElement>;
  @ViewChild('video')
  protected videoComponent?: ElementRef<HTMLVideoElement>;
  protected videoStyle: { [key: string]: any } = {};

  public ngAfterViewInit(): void {
    setTimeout(() => {
      this.updateDimensions();
    });
  }
  public handleVideoLoadedMetadata(): void {
    this.updateDimensions();
  }

  private updateDimensions(): void {
    if (
      this.videoContainerComponent === undefined ||
      this.videoComponent === undefined
    ) {
      return;
    }
    const videoElement = this.videoComponent.nativeElement;
    const videoContainerRect =
      this.videoContainerComponent.nativeElement.getBoundingClientRect();
    const videoAspectRatio =
      (videoElement.videoWidth ?? 0) / (videoElement.videoHeight ?? 0);
    const containerAspectRation =
      videoContainerRect.width / videoContainerRect.height;
    if (videoAspectRatio > containerAspectRation) {
      this.videoStyle = {
        width: `${videoContainerRect.width}px`,
        height: `${videoContainerRect.width * (1 / videoAspectRatio)}px`,
      };
    } else {
      this.videoStyle = {
        width: `${videoContainerRect.height * videoAspectRatio}px`,
        height: `${videoContainerRect.height}px`,
      };
    }
  }

  @HostListener('document:fullscreenchange')
  protected handleFullScreenChange() {
    setTimeout(() => this.updateDimensions());
  }

  @HostListener('window:resize')
  protected handleResize() {
    this.updateDimensions();
  }
}
