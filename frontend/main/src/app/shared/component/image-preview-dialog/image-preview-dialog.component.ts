import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { DynamicDialogConfig } from 'primeng/dynamicdialog';

export interface ImagePreviewDialogConfig {
  image: Blob;
}

@Component({
  selector: 'app-image-preview-dialog',
  imports: [CommonModule],
  templateUrl: './image-preview-dialog.component.html',
  styleUrl: './image-preview-dialog.component.scss',
})
export class ImagePreviewDialogComponent implements OnInit {
  private readonly config: DynamicDialogConfig<ImagePreviewDialogConfig> =
    inject(DynamicDialogConfig);
  private readonly domSanitizer = inject(DomSanitizer);

  protected imageUrl?: SafeUrl;

  public ngOnInit(): void {
    const reader = new FileReader();
    reader.onload = (): void => {
      const value = this.domSanitizer.bypassSecurityTrustUrl(
        reader.result as string
      );
      this.imageUrl = value;
    };
    reader.readAsDataURL(this.config.data!.image);
  }
}
