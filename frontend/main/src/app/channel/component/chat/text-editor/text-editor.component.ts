import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  inject,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import validation, {
  channelMessageTextMaxCharacters,
} from '../../../../shared/utils/validation';
import formUtils from '../../../../shared/utils/form-utils';
import { ToastService } from '../../../../shared/service/toast.service';
import { CreateChannelMessagePayload } from '../../../model/message/create-channel-message-payload.model';
import { v4 as uuidv4 } from 'uuid';
import { ChannelDetailsDto } from '../../../model/channel-detais-dto.model';
import { InternalMessageStatus } from '../../../enum/internal/internal-message-status.enum';
import { MemberProfileDto } from '../../../../user/model/member-profile-dto.model';
import { CurrentUserService } from '../../../../user/service/current-user.service';
import { forkJoin, Observable, of, Subscription, tap } from 'rxjs';
import { MessageDomTagProcessorService } from '../../../service/message-dom-tag-processor.service';
import { StompChannelService } from '../../../service/api/stomp-channel.service';
import { UnprocessedChannelMessageBasicsDto } from '../../../model/internal/message/unprocessed-channel-message-basics-dto';
import { WINDOW } from '../../../../core/token/window.token';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ProjectConstants } from '../../../../shared/constants/project-constants';
import { TypographyComponent } from '../../../../shared/component/typography/typography.component';
import mathUtils from '../../../../shared/utils/math-utils';
import { FileUploadModule } from 'primeng/fileupload';
import { TooltipModule } from 'primeng/tooltip';
import {
  ImagePreviewDialogComponent,
  ImagePreviewDialogConfig,
} from '../../../../shared/component/image-preview-dialog/image-preview-dialog.component';
import { DialogService } from 'primeng/dynamicdialog';
import { MemberChannelService } from '../../../service/api/member-channel.service';
import { ChannelFileDto } from '../../../model/file/channel-file-dto.model';

enum FileSendingStatus {
  EDITABLE = 'EDITABLE',
  PENDING = 'PENDING',
  SENT = 'SENT',
}

interface MessageForm {
  text: FormControl<string | null>;
  files: FormControl<File[] | null>;
}

interface DragImagesDetails {
  startX: number;
  startScroll: number;
  pastDelta: boolean;
}

interface ImageDetails {
  src: SafeUrl | null;
  status: FileSendingStatus;
}

@Component({
  selector: 'app-text-editor',
  imports: [
    CommonModule,
    ButtonModule,
    TranslateModule,
    RippleModule,
    ReactiveFormsModule,
    TypographyComponent,
    FileUploadModule,
    TooltipModule,
  ],
  templateUrl: './text-editor.component.html',
  styleUrl: './text-editor.component.scss',
})
export class TextEditorComponent implements OnInit, OnDestroy {
  protected readonly accept = ProjectConstants.ALLOWED_IMAGE_TYPES.join(', ');
  protected readonly imagesDeltaDrag = 10;

  @Input({ required: true }) public channel!: ChannelDetailsDto;
  @Output()
  public onMessageCreated =
    new EventEmitter<UnprocessedChannelMessageBasicsDto>();
  @ViewChild('textarea')
  protected set textarea(element: ElementRef<HTMLElement> | undefined) {
    this._textarea = element;
    this.previousHtmlValue = element?.nativeElement.innerHTML ?? '';
  }
  @ViewChild('textarea')
  protected editor?: ElementRef<HTMLElement>;
  @ViewChild('uploadedImages')
  protected uploadedImagesComponent?: ElementRef<HTMLElement>;

  private readonly window = inject(WINDOW);
  private readonly formBuilder = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly stompChannelService = inject(StompChannelService);
  private readonly currentUserService = inject(CurrentUserService);
  private readonly messageDomTagProcessorService = inject(
    MessageDomTagProcessorService
  );
  private readonly domSanitizer = inject(DomSanitizer);
  private readonly dialogService = inject(DialogService);
  private readonly memberChannelService = inject(MemberChannelService);

  private _textarea?: ElementRef<HTMLElement>;
  protected readonly form: FormGroup<MessageForm> = this.formBuilder.group({
    text: ['', validation.channelMessageText],
    files: [[] as File[]],
  });
  protected fileSendingStatus = FileSendingStatus;
  protected focused = false;
  protected sendEnabled = false;
  protected previousHtmlValue = '<br>';
  protected textContent: string | null = '';
  protected userProfile!: MemberProfileDto;
  protected filesDetails: ImageDetails[] = [];
  protected dragFocused = false;
  protected uploadFocused = false;
  protected dragNodes: Set<EventTarget> = new Set();
  protected sending = false;
  private imagesDragDetails: DragImagesDetails | null = null;
  private userProfileSubscription!: Subscription;
  private textValueChangesSubscription!: Subscription;
  private imagesValueChangesSubscription!: Subscription;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => (this.userProfile = userProfile));
    this.form.valueChanges;
    this.textValueChangesSubscription =
      this.form.controls.text.valueChanges.subscribe((value) =>
        this.updateSendEnabled(value, undefined)
      );
    this.imagesValueChangesSubscription =
      this.form.controls.files.valueChanges.subscribe((value) =>
        this.updateSendEnabled(undefined, value)
      );
  }

  public ngOnDestroy(): void {
    this.userProfileSubscription.unsubscribe();
    this.textValueChangesSubscription.unsubscribe();
    this.imagesValueChangesSubscription.unsubscribe();
  }

  protected handleFocus(): void {
    this.focused = true;
  }

  protected handleFocusOut(): void {
    this.focused = false;
  }

  protected handleChange(): void {
    const textareaElement = this._textarea?.nativeElement;
    if (textareaElement === undefined) {
      return;
    }
    const result = this.messageDomTagProcessorService.toTags(textareaElement);
    if (result.length >= channelMessageTextMaxCharacters) {
      textareaElement.innerHTML = this.previousHtmlValue;
    }
    this.previousHtmlValue = textareaElement.innerHTML;
    this.setTextValue(result);
  }

  protected handleKeydown(event: KeyboardEvent): void {
    if (event.key !== 'Enter' || event.shiftKey) {
      return;
    }
    event.preventDefault();
    if (!this.isBlank() && !this.sending) {
      this.onSubmit();
    }
  }

  protected onSubmit(): void {
    formUtils.validate(this.form);
    if (!this.form.valid) {
      if (this.form.controls.text.hasError('channelMessageText')) {
        this.toastService.displayErrorMessage(
          'error.validation.channelMessageText'
        );
      } else {
        this.toastService.displayErrorMessage('error.formInvalid');
      }
      return;
    }
    this.sending = true;
    const textareaElement = this._textarea?.nativeElement;
    const value = this.form.value!;

    value.files!.forEach(
      (_, index) =>
        (this.filesDetails[index].status = FileSendingStatus.PENDING)
    );
    var fileObservables: Observable<[number, ChannelFileDto]>[] =
      value.files!.map((file, index) => {
        return forkJoin([
          of(index),
          this.memberChannelService.uploadFile(this.channel.id, file),
        ]).pipe(
          tap((res) => {
            this.filesDetails[res[0]].status === FileSendingStatus.SENT;
          })
        );
      });
    const filesObservable =
      fileObservables.length > 0 ? forkJoin(fileObservables) : of([]);
    filesObservable.subscribe({
      next: (responses) => {
        const filesIds = responses.map((response) => ({ id: response[1].id }));
        const files = responses.map((response) => ({ ...response[1] }));
        const payload: CreateChannelMessagePayload = {
          text: value.text!,
          files: filesIds,
        };
        const transactionId = uuidv4();
        const message: UnprocessedChannelMessageBasicsDto = {
          transactionId: transactionId,
          internalStatus: InternalMessageStatus.SENDING,
          createdAt: new Date(),
          text: value.text!,
          textNodes: this.messageDomTagProcessorService.toTextNodes(
            value.text!
          ),
          member: {
            id: this.userProfile.id,
            nickname: this.userProfile.nickname,
            hasProfilePicture: this.userProfile.hasProfilePicture,
          },
          files,
        };
        this.onMessageCreated.emit(message);
        this.previousHtmlValue = '';
        if (textareaElement !== undefined) {
          textareaElement.innerHTML = '';
        }
        this.stompChannelService.createMessage(this.channel.id, payload, {
          transactionId: transactionId,
        });
        this.clear();
        this.sending = false;
      },
      error: (e) => {
        this.toastService.displayErrorMessage('channel.chat.filesUploadError');
        this.filesDetails.forEach(
          (fileDetails) => (fileDetails.status = FileSendingStatus.EDITABLE)
        );
      },
    });
  }

  protected isEmpty(): boolean {
    return this.textContent === null || this.textContent === '';
  }

  private isBlank(): boolean {
    return (this.form.value.text ?? '') === '';
  }

  private clear() {
    this.setTextValue('');
    this.form.controls.files.setValue([]);
    this.filesDetails = [];
  }

  private setTextValue(text: string): void {
    const textareaElement = this._textarea?.nativeElement;
    if (textareaElement !== undefined) {
      this.textContent = textareaElement.textContent;
    }
    this.form.controls.text.setValue(text);
  }

  private updateSendEnabled(
    text: string | null | undefined,
    files: File[] | null | undefined
  ) {
    const textValue = text === undefined ? this.form.value.text : text;
    const filesValue = files === undefined ? this.form.value.files : files;
    this.sendEnabled =
      (textValue !== null && textValue !== '') || (filesValue?.length ?? 0) > 0;
  }

  protected handleTextAreaDragEnter(event: DragEvent) {
    event.preventDefault();
    if (event.target === null) {
      return;
    }
    if (this.dragNodes.size === 0) {
      console.log(event.dataTransfer);
      if (event.dataTransfer !== null) {
        this.dragFocused = true;
        if (event.dataTransfer.types.includes('Files')) {
          this.uploadFocused = true;
        }
      }
    }
    this.dragNodes.add(event.target);
  }

  protected handleTextAreaDragLeave(event: DragEvent) {
    event.preventDefault();
    if (event.target === null) {
      return;
    }
    this.dragNodes.delete(event.target);
    if (this.dragNodes.size === 0) {
      this.dragFocused = false;
      this.uploadFocused = false;
    }
  }

  protected handleTextAreaDragOver(event: DragEvent) {
    event.preventDefault();
  }

  protected handleTextAreaDrop(event: DragEvent) {
    event.preventDefault();
    this.dragFocused = false;
    this.uploadFocused = false;
    this.dragNodes.clear();
    const text = event.dataTransfer?.getData('text');
    const files = [...(event.dataTransfer?.items ?? [])];
    if (text !== undefined) {
      this.insertTextAtSelection(text);
    }
    if (files.length >= 0) {
      const images = files
        .filter((file) =>
          ProjectConstants.ALLOWED_IMAGE_TYPES.includes(file.type)
        )
        .map((file) => file.getAsFile()!);
      this.saveFormImages(images);
    }
  }

  private saveFormImages(images: File[]) {
    const sizeExceeded = images.some(
      (image) => image.size > ProjectConstants.MAXIMUM_BODY_SIZE
    );
    if (sizeExceeded) {
      this.toastService.displayErrorMessage(
        'channel.chat.maxImageSizeExceeded',
        { size: ProjectConstants.MAXIMUM_BODY_SIZE / (1024 * 1024) + ' MB' }
      );
      return;
    }

    images.forEach((image) => this.addUploadedImage(image!));
  }

  protected handleImagesChosen(event: Event): void {
    const input = event.target as HTMLInputElement;
    const images = [...(input.files ?? [])];
    this.saveFormImages(images);
  }

  private addUploadedImage(file: File): void {
    const currentValue = this.form.value.files ?? [];
    const reader = new FileReader();
    const index = currentValue.length;
    this.form.controls.files.setValue([...currentValue, file]);
    this.filesDetails.push({
      src: null,
      status: FileSendingStatus.EDITABLE,
    });
    reader.onload = (): void => {
      if (this.filesDetails[index] === undefined) {
        return;
      }
      const value = this.domSanitizer.bypassSecurityTrustUrl(
        reader.result as string
      );
      this.filesDetails[index].src = value;
    };
    reader.readAsDataURL(file);
  }

  protected removeImage(index: number) {
    const currentValue = this.form.value.files ?? [];
    if (
      index >= currentValue.length ||
      this.imagesDragDetails?.pastDelta === true
    ) {
      return;
    }
    this.filesDetails.splice(index, 1);
    currentValue.splice(index, 1);
    this.form.controls.files.setValue([...currentValue]);
  }

  protected openImagePreview(index: number) {
    const images = this.form.value.files ?? [];
    if (
      index >= images.length ||
      this.imagesDragDetails?.pastDelta === true ||
      images[index] === null
    ) {
      return;
    }
    const data: ImagePreviewDialogConfig = {
      image: images[index],
    };
    this.dialogService.open(ImagePreviewDialogComponent, {
      closable: true,
      modal: true,
      width: 'calc(100vw - 15rem)' as string,
      closeOnEscape: true,
      dismissableMask: true,
      focusOnShow: false,
      data,
    });
  }

  private insertTextAtSelection(text: string): void {
    if (this._textarea === undefined) {
      return;
    }
    const selection = this.window.getSelection();
    if (
      selection === null ||
      selection.rangeCount === 0 ||
      !this.isTextAreaChild(selection.anchorNode)
    ) {
      this._textarea.nativeElement.textContent += text;
      this.handleChange();
      return;
    }
    selection.deleteFromDocument();

    const range = selection.getRangeAt(0);
    const textNode = document.createTextNode(text);
    range.insertNode(textNode);
    range.setStartAfter(textNode);
    range.setEndAfter(textNode);
    selection.removeAllRanges();
    selection.addRange(range);
    this.handleChange();
  }

  private isTextAreaChild(node: Node | null | undefined): boolean {
    if (this._textarea === undefined || node === null || node === undefined) {
      return false;
    }
    while (node) {
      if (node === this._textarea.nativeElement) {
        return true;
      }
      node = node.parentNode;
    }
    return false;
  }

  protected getEditorClass(): string {
    const classes: string[] = [];
    if (this.focused || this.dragFocused || this.uploadFocused) {
      classes.push('editor--focused');
    }
    if (this.uploadFocused) {
      classes.push('editor--upload-focused');
    }
    if ((this.form.value.files?.length ?? 0) > 0) {
      classes.push('editor--images');
    }
    return classes.join(' ');
  }

  protected handleImagesDragStart(event: PointerEvent) {
    if (this.sending || this.uploadedImagesComponent === undefined) {
      return;
    }
    this.imagesDragDetails = {
      startX: event.clientX,
      startScroll: this.uploadedImagesComponent.nativeElement.scrollLeft,
      pastDelta: false,
    };
  }

  @HostListener('document:pointerup')
  @HostListener('document:pointercancel')
  protected onPointerRelease(): void {
    // next pass, so that click events can fire first
    setTimeout(() => {
      this.imagesDragDetails = null;
    });
  }

  @HostListener('document:pointermove', ['$event'])
  protected onContentDrag(event: PointerEvent): void {
    if (
      this.sending ||
      this.uploadedImagesComponent === undefined ||
      this.imagesDragDetails === null
    ) {
      return;
    }
    const element = this.uploadedImagesComponent.nativeElement;
    const delta = this.imagesDragDetails.startX - event.clientX;
    if (
      !this.imagesDragDetails.pastDelta &&
      Math.abs(delta) > this.imagesDeltaDrag
    ) {
      this.imagesDragDetails.pastDelta = true;
    }

    const result = mathUtils.clamp(
      delta + this.imagesDragDetails.startScroll,
      0,
      element.scrollWidth
    );
    this.uploadedImagesComponent.nativeElement.scrollTo(result, 0);
  }

  protected toggleBold(): void {
    const selection: Selection | null = window.getSelection();
    if (!selection || selection.rangeCount === 0) return;

    const range: Range = selection.getRangeAt(0);
    if (range.collapsed) return; // nothing is selected

    // Determine whether all text nodes intersecting the selection are already bold.
    let allBold = true;
    const treeWalker: TreeWalker = document.createTreeWalker(
      range.commonAncestorContainer,
      NodeFilter.SHOW_TEXT,
      {
        acceptNode(node: Node): number {
          // For each text node, create a range covering its contents.
          const textNode = node as Text;
          const nodeRange: Range = document.createRange();
          nodeRange.selectNodeContents(textNode);
          // Accept the node if its range intersects with the selection range.
          if (
            range.compareBoundaryPoints(Range.END_TO_START, nodeRange) < 0 &&
            range.compareBoundaryPoints(Range.START_TO_END, nodeRange) > 0
          ) {
            return NodeFilter.FILTER_ACCEPT;
          }
          return NodeFilter.FILTER_REJECT;
        },
      }
    );

    let textNode: Node | null;
    while ((textNode = treeWalker.nextNode())) {
      if (!this.isBoldNode(textNode)) {
        allBold = false;
        break;
      }
    }

    if (allBold) {
      this.unboldRange(range);
    } else {
      this.boldRange(range);
    }

    // Optionally clear the selection after the operation.
    selection.removeAllRanges();
  }

  // Returns true if the given node is within a <b> tag.
  private isBoldNode(node: Node): boolean {
    let parent: HTMLElement | null = node.parentElement;
    while (parent) {
      if (parent.tagName.toLowerCase() === 'b') {
        return true;
      }
      parent = parent.parentElement;
    }
    return false;
  }

  // Wrap the contents of the range in a <b> element.
  private boldRange(range: Range): void {
    const extracted: DocumentFragment = range.extractContents();
    const boldElem: HTMLElement = document.createElement('b');
    boldElem.appendChild(extracted);
    range.insertNode(boldElem);
  }

  // Remove bold formatting from the contents of the range.
  private unboldRange(range: Range): void {
    const extracted: DocumentFragment = range.extractContents();
    this.unboldFragment(extracted);
    range.insertNode(extracted);
  }

  // Recursively unwrap any <b> elements in the given fragment.
  private unboldFragment(fragment: DocumentFragment): void {
    const boldElements: NodeListOf<HTMLElement> =
      fragment.querySelectorAll('b');
    boldElements.forEach((b: HTMLElement) => {
      // Move all child nodes of the <b> element to its parent.
      while (b.firstChild) {
        if (b.parentNode) {
          b.parentNode.insertBefore(b.firstChild, b);
        }
      }
      // Remove the empty <b> element.
      if (b.parentNode) {
        b.parentNode.removeChild(b);
      }
    });
  }
}
