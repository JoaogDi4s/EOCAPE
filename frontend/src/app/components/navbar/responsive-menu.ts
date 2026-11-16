import { DestroyRef, Directive, ElementRef, inject, signal } from '@angular/core';

@Directive({
  selector: '[appResponsiveMenu]',
  exportAs: 'responsiveMenu',
  host: {
    '[class.menu-open]': 'open()',
    '(document:click)': 'onClick($event)',
    '(keydown.escape)': 'close(true)',
    '(focusout)': 'onFocusOut($event)',
  },
})
export class ResponsiveMenu {
  readonly open = signal(false);
  private host = inject<ElementRef<HTMLElement>>(ElementRef).nativeElement;

  constructor() {
    const compact = window.matchMedia?.('(max-width: 850px)');
    const reset = () => this.close();
    compact?.addEventListener('change', reset);
    inject(DestroyRef).onDestroy(() => compact?.removeEventListener('change', reset));
  }

  toggle() {
    this.open.update((open) => !open);
  }

  close(restoreFocus = false) {
    if (!this.open()) return;
    this.open.set(false);
    if (restoreFocus)
      this.host.querySelector<HTMLButtonElement>('.menu-toggle')?.focus({ preventScroll: true });
  }

  onClick(event: MouseEvent) {
    if (!this.open() || !(event.target instanceof Element)) return;
    if (!this.host.contains(event.target)) this.close();
    else if (event.target.closest('.menu-links a, .menu-links button')) this.close(true);
  }

  onFocusOut(event: FocusEvent) {
    if (event.relatedTarget instanceof Node && !this.host.contains(event.relatedTarget))
      this.close();
  }
}
