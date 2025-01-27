import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { ContainerComponent } from '../../../shared/component/container/container.component';
import { ButtonModule } from 'primeng/button';
import { ActivatedRoute, Router } from '@angular/router';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';

@Component({
  selector: 'app-error',
  imports: [
    CommonModule,
    TranslateModule,
    ContainerComponent,
    ButtonModule,
    TypographyComponent,
  ],
  templateUrl: './error.component.html',
  styleUrl: './error.component.scss',
})
export class ErrorComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected titleKey?: string;

  public ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) => {
      this.titleKey = data['titleKey'];
    });
  }

  protected goBack() {
    this.router.navigate(['/']);
  }
}
