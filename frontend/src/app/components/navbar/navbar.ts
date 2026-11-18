import { ThemeToggle } from '../theme-toggle/theme-toggle';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResponsiveMenu } from './responsive-menu';

@Component({
  imports: [ThemeToggle, RouterLink, ResponsiveMenu],
  selector: 'app-navbar',
  templateUrl: './navbar.html',
})
export class Navbar {}
