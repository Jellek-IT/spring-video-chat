import {
  Directive,
  EmbeddedViewRef,
  Input,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';

export interface LetContext<T> {
  $implicit: T;
  appLet: T;
}

@Directive({
  selector: '[appLet]',
  standalone: true,
})
export class LetDirective<T> {
  @Input('appLet') value!: T;
  view!: EmbeddedViewRef<any>;
  context: LetContext<T | null> = { $implicit: null, appLet: null };

  constructor(
    private vcRef: ViewContainerRef,
    private templateRef: TemplateRef<any>
  ) {}

  static ngTemplateContextGuard<T>(
    dir: LetDirective<T>,
    ctx: any
  ): ctx is LetContext<T> {
    return true;
  }

  public ngOnInit() {
    this.updateContext();
    this.view = this.vcRef.createEmbeddedView(this.templateRef, this.context);
  }

  public ngOnChanges() {
    if (!this.view || this.value === this.context.appLet) {
      return;
    }
    this.updateContext();
    (this.view.context as LetContext<T | null>).$implicit =
      this.context.$implicit;
    (this.view.context as LetContext<T | null>).appLet = this.context.appLet;
    this.view.markForCheck();
  }

  private updateContext() {
    this.context = {
      $implicit: this.value,
      appLet: this.value,
    };
  }
}
