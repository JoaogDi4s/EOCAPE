import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResponsiveMenu } from './responsive-menu';

@Component({
  imports: [RouterLink, ResponsiveMenu],
  selector: 'app-navbar',
  templateUrl: './navbar.html',
})
export class Navbar {}
