import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ICalendar} from "../shared/model/calendar";

@Component({
  selector: 'sc-calendar-landing',
  templateUrl: './calendar-landing.component.html',
  styleUrls: ['./calendar-landing.component.scss']
})
export class CalendarLandingComponent implements OnInit {

  private calendar: ICalendar;

  constructor(
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.data.subscribe((data: { calendar: ICalendar }) => {
      this.calendar = data.calendar;
    });
  }

}
