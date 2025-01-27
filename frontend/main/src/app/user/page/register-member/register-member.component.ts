import { Component, inject, OnInit } from '@angular/core';
import { ContainerComponent } from '../../../shared/component/container/container.component';
import { ValidationMessageComponent } from '../../../shared/component/validation-message/validation-message.component';
import { TranslateModule } from '@ngx-translate/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import validation from '../../../shared/utils/validation';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import formUtils from '../../../shared/utils/form-utils';
import { ToastService } from '../../../shared/service/toast.service';
import { RegisterMemberRequest } from '../../model/register-member-request.model';
import { EndpointErrorService } from '../../../error/service/endpoint-error.service';
import { CommonModule } from '@angular/common';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';
import Keycloak from 'keycloak-js';
import { environment } from '../../../../environments/environment';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { PublicMemberService } from '../../service/api/public-member.service';

interface RegisterForm {
  email: FormControl<string | null>;
  nickname: FormControl<string | null>;
  password: FormControl<string | null>;
}

@Component({
  selector: 'app-register',
  imports: [
    CardModule,
    CommonModule,
    ContainerComponent,
    ValidationMessageComponent,
    TranslateModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    ReactiveFormsModule,
    TypographyComponent,
  ],
  templateUrl: './register-member.component.html',
  styleUrl: './register-member.component.scss',
})
export class RegisterMemberComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly publicMemberService = inject(PublicMemberService);
  private readonly endpointErrorService = inject(EndpointErrorService);
  private readonly keycloak = inject(Keycloak);
  private readonly router = inject(Router);
  protected loading = false;
  protected resultNickname: string | null = null;
  protected resultError: string | null = null;

  protected form: FormGroup<RegisterForm> = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    nickname: ['', validation.userNickname],
    password: ['', validation.userPassword],
  });

  public ngOnInit(): void {
    if (this.keycloak.authenticated) {
      this.router.navigate(['/']);
    }
  }

  protected onSubmit() {
    formUtils.validate(this.form);
    if (!this.form.valid) {
      this.toastService.displayErrorMessage('error.formInvalid');
      return;
    }
    const value = this.form.value!;
    const registerMemberRequest: RegisterMemberRequest = {
      email: value.email!,
      nickname: value.nickname!,
      password: value.password!,
    };
    this.resultNickname = null;
    this.resultError = null;
    this.loading = true;
    this.publicMemberService.register(registerMemberRequest).subscribe({
      next: (res) => {
        this.loading = false;
        this.resultNickname = res.nickname;
      },
      error: (e) => {
        this.endpointErrorService.getErrorText(e).subscribe((errorText) => {
          this.resultError = errorText;
          this.loading = false;
        });
      },
    });
  }

  protected login() {
    this.keycloak.login({ redirectUri: environment.url });
  }
}
