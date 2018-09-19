import {Component, ElementRef, Input, NgZone, OnInit, Renderer2, ViewChild} from '@angular/core';
import {Observable} from "rxjs";
import {ICalendar} from "../model/calendar";
import {CalendarService} from "../calendar.service";
// import {animate, state, style, transition, trigger} from "@angular/animations";

// WIP Navbar collapse using angular animations
// const Collapse = [
//   trigger('collapse', [
//     state('open', style({
//       'height': "*",
//       'visibility': 'visible',
//       'overflow-y': 'hidden'
//     })),
//     state('closed', style({
//       'height': '0',
//       'visibility': 'hidden',
//       'overflow-y': 'hidden'
//     })),
//     transition('open <=> closed', [animate('2s ease')])
//   ]),
// ];

@Component({
  selector: 'sc-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
  // ,animations: [Collapse]
})
export class NavbarComponent implements OnInit {

  @Input() title: string = "Calendars";
  @ViewChild('navCollapse') navCollapse: ElementRef;

  navbarOpen = false;
  navbarClosed = true;
  navbarOpening = false;
  navbarClosing = false;

  calendars$: Observable<ICalendar[]>;

  constructor(
    private ngZone: NgZone,
    private calendarService: CalendarService,
    private renderer2: Renderer2) { }


  ngOnInit(): void {
    this.calendars$ = this.calendarService.calendars;
  }

  // This is a hack to add the bootstrap collapse animation to the navbar as it's not
  // yet supported in ng-bootstrap
  onTransitionEnd() {
    if (this.navbarClosing) {
      this.navbarClosed = true;
      this.navbarOpen = this.navbarOpening = this.navbarClosing = false;
    } else if (this.navbarOpening) {
      this.navCollapse.nativeElement.style.height = '';
      this.navbarOpen = true;
      this.navbarClosed = this.navbarOpening = this.navbarClosing = false;
    }
  }

  toggleNavbar(): void {
    // this.isNavbarCollapsed = !this.isNavbarCollapsed;
    let el = this.navCollapse.nativeElement;
    if (this.navbarOpen) {
      let height = el.getBoundingClientRect().height;
      this.renderer2.setStyle(el, 'height', `${height}px`);
      this.navbarOpen = this.navbarClosed = this.navbarOpening = false;
      this.navbarClosing = true;
      // need to wait a tick for classes based on booleans to update
      this.ngZone.runOutsideAngular(() =>
        setTimeout(() => {
          this.renderer2.setStyle(el,'height', '');
        }, 0)
      );
    } else if (this.navbarClosed) {
      this.renderer2.setStyle(el, 'height', 0);
      this.navbarOpen = this.navbarClosed = this.navbarClosing = false;
      this.navbarOpening = true;
      // need to wait a tick for classes based on booleans to update
      this.ngZone.runOutsideAngular(() =>
        setTimeout(() => {
          let scrollHeight = el.scrollHeight;
          this.renderer2.setStyle(el, 'height', `${scrollHeight}px`);
        }, 0)
      );
    }
  }


}
