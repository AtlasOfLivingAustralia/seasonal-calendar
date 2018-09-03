import { Component, OnInit } from '@angular/core';
import { CalendarService } from "../calendar.service";
import { Calendar } from "../model/calendar";
import { Observable } from "rxjs";

@Component({
  selector: 'sc-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  calendars$: Observable<Calendar[]>;

  constructor(private calendarService: CalendarService) { }

  ngOnInit() {
    this.calendars$ = this.calendarService.getCalendars();
  }

}
