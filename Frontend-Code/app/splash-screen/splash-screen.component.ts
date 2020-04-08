import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { trigger, state, style, animate, transition, keyframes } from '@angular/animations';

@Component({
  selector: 'app-splash-screen',
  templateUrl: './splash-screen.component.html',
  styleUrls: ['./splash-screen.component.css'],
  animations: [
    trigger('fade', [
      state('enter', style({
        opacity: '1'
      })),
      state('leave', style({
        opacity: '0'
      })),
      transition('*=>enter', [
        animate('1s')
      ]),
      transition('*=> leave', [
        animate('1s')
      ])
    ])
  ]
})
export class SplashScreenComponent implements OnInit {
  isappear: Boolean = true;
  logo_appear: Boolean = true;
  constructor(private router: Router) { }

  ngOnInit() {
    setTimeout(() => {
      this.isappear = false;
      this.logo_appear = true;

    }, 2000)
    setTimeout(() => {
      this.router.navigate(['login']);

    }, 4000);  //5s
  }

}
