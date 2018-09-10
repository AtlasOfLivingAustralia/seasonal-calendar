import { Component, OnInit } from '@angular/core';
import {ICalendar} from '../model/calendar';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'sc-calendars-gallery',
  templateUrl: './calendars-gallery.component.html',
  styleUrls: ['./calendars-gallery.component.scss']
})
export class CalendarsGalleryComponent implements OnInit {

  calendars: ICalendar[];

  constructor(private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {

    this.route.data.subscribe((data: { calendars: ICalendar[] }) => {
      this.calendars = data.calendars;
    });
    // this.calendars$ = this.calendarService.calendars;
  }
}
