import {Component, ElementRef, Input, NgZone, OnInit, Renderer2, ViewChild} from '@angular/core';
import {Observable} from "rxjs";
import {ICalendar} from "../shared/model/calendar";
import {CalendarService, UserInfoService} from "../shared/services/calendar.service";
import {OidcSecurityService} from "angular-auth-oidc-client";
import {canEditAnyCalendar} from "../shared/roles";
import {map} from "rxjs/operators";
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
  templateUrl: './navbar.component.html'
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

  isLoggedIn$: Observable<Boolean>;
  canAccessAdmin$: Observable<Boolean>;

  constructor(
    private ngZone: NgZone,
    private calendarService: CalendarService,
    private renderer2: Renderer2,
    private oidcSecurityService: OidcSecurityService,
    private userInfoService: UserInfoService) {
    this.isLoggedIn$ = this.oidcSecurityService.getIsAuthorized();
    this.canAccessAdmin$ = userInfoService.userinfo$.pipe(
      map(info => canEditAnyCalendar(info))
    );
    // this.oidcSecurityService.onAuthorizationResult.subscribe(next => console.log(next));
  }


  ngOnInit(): void {
    this.calendars$ = this.calendarService.getCalendars(true);
  }

  login() {
    this.oidcSecurityService.authorize();
    // this.oidcSecurityService.authorize((authUrl) => {
    //   // handle the authorization URL
    //   window.open(authUrl, 'sc_login', 'toolbar=0,location=0,menubar=0');
    // });
  }

  logout() {
    this.oidcSecurityService.logoff();
  }

  // This is a hack to add the bootstrap collapse animation to the navbar as it's not
  // yet supported in ng-bootstrap
  onTransitionEnd() {
    if (this.navbarClosing) {
      this.navbarClosed = true;
      this.navbarOpen = this.navbarOpening = this.navbarClosing = false;
    } else if (this.navbarOpening) {
      this.renderer2.setStyle(this.navCollapse.nativeElement, 'height', '');
      // this.navCollapse.nativeElement.style.height = '';
      this.navbarOpen = true;
      this.navbarClosed = this.navbarOpening = this.navbarClosing = false;
    }
  }

  toggleNavbar(): void {
    // this.isNavbarCollapsed = !this.isNavbarCollapsed;
    let el = this.navCollapse.nativeElement;
    if (this.navbarOpen) {
      let height = el ? el.getBoundingClientRect().height : 0;
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
          let scrollHeight = el ? el.scrollHeight : 0;
          this.renderer2.setStyle(el, 'height', `${scrollHeight}px`);
        }, 0)
      );
    }
  }


}
