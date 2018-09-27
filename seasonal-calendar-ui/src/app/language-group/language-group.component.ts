import { Component, OnInit } from '@angular/core';
import {ICalendar} from '../model/calendar';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from "../../environments/environment";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Component({
  selector: 'sc-language-group',
  templateUrl: './language-group.component.html',
  styleUrls: ['./language-group.component.scss']
})
export class LanguageGroupComponent implements OnInit {

  calendar: ICalendar;
  profileLink: String;

  welcomeMedia: SafeResourceUrl;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.route.data.subscribe((data: { calendar: ICalendar }) => {
      this.calendar = data.calendar;
      this.welcomeMedia = this.sanitizer.bypassSecurityTrustResourceUrl(data.calendar.welcomeCountryMedia);
      this.profileLink = `${environment.profiles}opus/${this.calendar.collectionUuid}`;
    });
  }
  getSanitizedUrl(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }

}
