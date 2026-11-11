import { Component } from '@angular/core';

@Component({
  selector: 'app-whatsapp-button',
  templateUrl: './whatsapp-button.html',
  host: { class: 'sticky bottom-6 z-40 mb-6 block h-0' },
})
export class WhatsappButton {
  // código do país + DDD + número, só dígitos
  numero = '5515996620286';
  mensagem = 'Olá! Gostaria de saber mais sobre o EOCAPE.';

  get link() {
    return `https://wa.me/${this.numero}?text=${encodeURIComponent(this.mensagem)}`;
  }
}
