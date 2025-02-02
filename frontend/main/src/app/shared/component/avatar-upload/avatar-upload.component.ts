import {
  Component,
  ElementRef,
  EventEmitter,
  Output,
  ViewChild,
} from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import {
  FileSelectEvent,
  FileUpload,
  FileUploadModule,
} from 'primeng/fileupload';
import { TooltipModule } from 'primeng/tooltip';
import { TypographyComponent } from '../typography/typography.component';
import {
  ImageCropperComponent,
  ImageCroppedEvent,
  OutputFormat,
} from 'ngx-image-cropper';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../loader/loader.component';
import mathUtils from '../../utils/math-utils';
import { ProjectConstants } from '../../constants/project-constants';

@Component({
  selector: 'app-avatar-upload',
  imports: [
    FileUploadModule,
    ButtonModule,
    TooltipModule,
    TranslateModule,
    TypographyComponent,
    ImageCropperComponent,
    CommonModule,
    LoaderComponent,
  ],
  templateUrl: './avatar-upload.component.html',
  styleUrl: './avatar-upload.component.scss',
})
export class AvatarUploadComponent {
  protected readonly maximumSize = ProjectConstants.MAXIMUM_BODY_SIZE;
  protected readonly accept = ProjectConstants.ALLOWED_IMAGE_TYPES.join(', ');
  private readonly maxHeight = 400;

  @Output()
  public onImageUpload = new EventEmitter<Blob>();

  @ViewChild('content')
  protected contentComponent?: ElementRef<HTMLElement>;
  @ViewChild('fileupload')
  protected fileuploadComponent?: FileUpload;
  protected selectedFile: File | null = null;
  protected croppedImage: Blob | null = null;
  protected cropperWidth = '100%';
  protected loading = false;

  protected handleFileSelected(event: FileSelectEvent): void {
    this.selectedFile === null;
    const file = event.currentFiles[0];
    const reader = new FileReader();
    reader.onload = (e) => {
      const image = new Image();
      image.onload = () => {
        this.selectedFile = file;
        const contentWidth =
          this.contentComponent?.nativeElement.getBoundingClientRect().width ??
          0;
        const aspectRatio = image.naturalWidth / image.naturalHeight;
        this.cropperWidth = `${
          mathUtils.clamp((this.maxHeight * aspectRatio) / contentWidth, 0, 1) *
          100
        }%`;
      };
      image.src = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }

  protected handleClear(callback: VoidFunction): void {
    callback();
    this.selectedFile = null;
    this.croppedImage = null;
    this.cropperWidth = '100%';
  }

  protected handleUpload(callback: VoidFunction): void {
    if (this.croppedImage === null) {
      return;
    }
    callback();
    this.loading = true;
    this.onImageUpload.emit(this.croppedImage);
  }

  public uploadEnded() {
    this.loading = false;
    this.selectedFile = null;
    this.croppedImage = null;
    this.cropperWidth = '100%';
    this.fileuploadComponent?.clear();
  }

  public uploadError() {
    this.loading = false;
  }

  protected handleImageCropped(event: ImageCroppedEvent): void {
    if (event.blob !== undefined) {
      this.croppedImage = event.blob;
    }
  }

  protected getCropperOutputFormat(): OutputFormat | undefined {
    if (this.selectedFile === null) {
      return undefined;
    }
    switch (this.selectedFile.type) {
      case 'image/png':
        return 'png';
      case 'image/jpeg':
        return 'jpeg';
      default:
        return undefined;
    }
  }
}
