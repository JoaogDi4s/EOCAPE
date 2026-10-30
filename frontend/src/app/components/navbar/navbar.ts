import { Component, ElementRef, HostListener, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { whiteButton } from '../WhiteButton/whiteButton';

@Component({
  imports: [RouterLink, whiteButton],
  selector: 'app-navbar',
  templateUrl: './navbar.html',
})

export class Navbar {
  private el = inject(ElementRef);
  menuAberto = false;
  @HostListener('document:click', ['$event']) aoClicarFora(event: MouseEvent) {
    if (this.menuAberto && !event.composedPath().includes(this.el.nativeElement)) {
      this.menuAberto = false;
    }
  }
  irPara(id: string) {
    this.menuAberto = false;
    setTimeout(() => {
      document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
    });
  }
  @HostListener('document:keydown.escape') aoApertarEsc() {
    this.menuAberto = false;
  }
}
