import { Component, OnInit } from '@angular/core';
import {ICalendar} from '../shared/model/calendar';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'sc-calendars-gallery',
  templateUrl: './calendars-gallery.component.html'
})
export class CalendarsGalleryComponent implements OnInit {

  calendars: ICalendar[];
  public lat = -35.57;
  public lon = 149.123;
  public zoom = 8;

  constructor(private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {

    this.route.data.subscribe((data: { calendars: ICalendar[] }) => {
      this.calendars = data.calendars;
    });
  }
}
