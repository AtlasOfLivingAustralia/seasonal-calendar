import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { CalendarService } from './calendar.service';
import {ICalendar} from './model/calendar';

@Component({
  selector: 'sc-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'seasonal-calendar-ui';

  calendars$: Observable<ICalendar[]>;

  constructor(private calendarService: CalendarService) {
  }

  ngOnInit(): void {
    this.calendars$ = this.calendarService.calendars;
  }
}
