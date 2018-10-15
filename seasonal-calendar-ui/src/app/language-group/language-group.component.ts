import { Component, OnInit } from '@angular/core';
import {ICalendar} from '../shared/model/calendar';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from "../../environments/environment";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Component({
  selector: 'sc-language-group',
  templateUrl: './language-group.component.html',
  styleUrls: ['./language-group.component.scss']
})
export class LanguageGroupComponent implements OnInit {

  public static readonly youTubeUrl = 'http://www.youtube.com/embed/';

  calendar: ICalendar;
  profileLink: String;

  welcomeMedia: SafeResourceUrl;
  mediaLinks: SafeResourceUrl[] = [];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.route.data.subscribe((data: { calendar: ICalendar }) => {
      this.calendar = data.calendar;
      let welcomeMediaUrl = this.formatUrl(data.calendar.welcomeCountryMedia);
      this.welcomeMedia = this.sanitizer.bypassSecurityTrustResourceUrl(welcomeMediaUrl);
      for (let i of this.calendar.mediaLinks){
        this.mediaLinks.push(this.sanitizer.bypassSecurityTrustResourceUrl(this.formatUrl(i)));
      }
     this.profileLink = `${environment.profiles}opus/${this.calendar.collectionUuid}`;
    });
  }

  private formatUrl(url: string): string {
    let parts = url.match(/v=(.*)(?:&)?/);
    if (parts && parts.length > 1) {
      return `${LanguageGroupComponent.youTubeUrl}${parts[1]}`;
    } else {
      return url;
    }
  }

}
