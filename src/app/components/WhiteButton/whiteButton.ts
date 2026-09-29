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
}