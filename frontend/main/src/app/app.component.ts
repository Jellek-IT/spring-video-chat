import { Component, inject, OnInit } from '@angular/core';
import { ToastModule } from 'primeng/toast';
import { PrimeNG } from 'primeng/config';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [
    CommonModule,
    ToastModule,
    RouterModule,
    ConfirmDialogModule,
    TranslateModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  public title = 'springchat-frontendmain';

  private readonly primeNG = inject(PrimeNG);
  private readonly translateService = inject(TranslateService);

  ngOnInit(): void {
    this.primeNG.ripple.set(true);
    this.translateService
      .get('primeng')
      .subscribe((res: any) => this.primeNG.setTranslation(res));
  }
}
