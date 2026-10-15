import { Component, input, output } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-button',
  templateUrl: './whiteButton.html',
})
export class whiteButton {
  type = input<'button' | 'submit'>('button');
  disabled = input(false);
  clicked = output<void>();
  buttonClass = input(
    'rounded-3xl bg-eocape-white px-10 py-3 text-xl font-black text-eocape-dark-blue shadow-lg transition hover:scale-105 focus:outline-none focus:ring-2 focus:ring-eocape-white focus:ring-offset-2 focus:ring-offset-eocape-light-blue disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:scale-100',
  );
}
