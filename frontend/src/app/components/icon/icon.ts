import { Component, computed, input } from '@angular/core';
/** Ícones vetoriais decorativos; o texto ou aria-label do controle fornece o nome acessível. */
export const ICON_PATHS = {
  home: ['M3 10 12 3l9 7v10a1 1 0 0 1-1 1h-5v-7H9v7H4a1 1 0 0 1-1-1Z'],
  settings: [
    'M9 3h6l1 3 3 1 2 5-2 5-3 1-1 3H9l-1-3-3-1-2-5 2-5 3-1Z',
    'M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z',
  ],
  info: ['M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z', 'M12 11v6', 'M12 7h.01'],
  'log-out': ['M9 4H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h4', 'M9 12h12m-4-4 4 4-4 4'],
  dashboard: ['M3 3h7v7H3ZM14 3h7v7h-7ZM3 14h7v7H3ZM14 14h7v7h-7Z'],
  users: [
    'M15 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0Z',
    'M3 21v-2a5 5 0 0 1 5-5h6a5 5 0 0 1 5 5v2',
    'M18 3a4 4 0 0 1 0 8m3 10v-2a5 5 0 0 0-3-4.6',
  ],
  building: [
    'M4 21V5a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v16M17 9h3a1 1 0 0 1 1 1v11M2 21h20',
    'M8 7h1m3 0h1M8 11h1m3 0h1M8 15h1m3 0h1M9 21v-3h3v3',
  ],
  'message-circle': [
    'M21 11.5a9 9 0 0 1-9 9 10 10 0 0 1-4-.9L3 21l1.4-4.8a9 9 0 1 1 16.6-4.7Z',
    'M8 9h8M8 13h5',
  ],
  'shopping-bag': ['M4 7h16l1 14H3ZM8 8V6a4 4 0 0 1 8 0v2'],
  'file-text': [
    'M14 2H5a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V9ZM14 2v7h7',
    'M7 13h10M7 17h8',
  ],
  lightbulb: ['M9 18h6m-6 3h6M8 14a6 6 0 1 1 8 0c-1 1-1 2-1 3H9c0-1 0-2-1-3Z'],
  package: ['M3 6 12 2l9 4v12l-9 4-9-4ZM3 6l9 4 9-4M12 10v12M7.5 4l9 4v5'],
  megaphone: ['M3 10v4h4l12 5V5L7 10ZM7 14l2 7h3l-2-6M21 9v6'],
  calendar: [
    'M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2ZM7 2v4m10-4v4M3 10h18',
    'M8 16l3 3 5-6',
  ],
  wrench: [
    'M14.5 6.5 18 3a6 6 0 0 0-7.8 7.8L3 18a2.1 2.1 0 0 0 3 3l7.2-7.2A6 6 0 0 0 21 6l-3.5 3.5Z',
  ],
  search: ['M16 10a6 6 0 1 1-12 0 6 6 0 0 1 12 0ZM14.5 14.5 21 21'],
  'arrow-left': ['M19 12H5m6-6-6 6 6 6'],
  'arrow-right': ['M5 12h14m-6-6 6 6-6 6'],
  plus: ['M12 5v14M5 12h14'],
  camera: [
    'M8 5l2-3h4l2 3h4a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z',
    'M16 13a4 4 0 1 1-8 0 4 4 0 0 1 8 0Z',
  ],
  image: [
    'M4 3h16a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z',
    'M9 8a1 1 0 1 1-2 0 1 1 0 0 1 2 0ZM3 17l6-6 4 4 3-3 5 5',
  ],
  check: ['M4 12l5 5L20 6'],
  'alert-circle': ['M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0ZM12 7v6m0 4h.01'],
} as const;
export type IconName = keyof typeof ICON_PATHS;
@Component({
  selector: 'app-icon',
  template:
    '<svg aria-hidden="true" focusable="false" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">@for(path of paths();track $index){<path [attr.d]="path" />}</svg>',
  styles: [
    ':host{display:inline-flex;align-items:center;justify-content:center;width:1.25em;height:1.25em;flex-shrink:0;vertical-align:middle;line-height:1}svg{display:block;width:100%;height:100%}',
  ],
})
export class Icon {
  name = input.required<IconName>();
  readonly paths = computed(() => ICON_PATHS[this.name()]);
}
