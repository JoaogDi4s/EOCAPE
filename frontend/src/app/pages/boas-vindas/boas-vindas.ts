import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-boas-vindas',
  templateUrl: './boas-vindas.html',
})
export class BoasVindas implements OnInit, OnDestroy {
  private readonly router = inject(Router);
  private startFadeTimer?: ReturnType<typeof setTimeout>;
  private navigateTimer?: ReturnType<typeof setTimeout>;

  saindo = signal(false);

  ngOnInit() {
    this.startFadeTimer = setTimeout(() => {
      this.saindo.set(true);
      this.navigateTimer = setTimeout(() => {
        this.router.navigate(['/user-page'], { replaceUrl: true });
      }, 500);
    }, 2000);
  }

  ngOnDestroy() {
    clearTimeout(this.startFadeTimer);
    clearTimeout(this.navigateTimer);
  }
}
