import { Component, OnInit } from '@angular/core';
import {Calendar} from "../model/calendar";
import {ActivatedRoute, Router} from "@angular/router";
import {Season} from "../model/season";
import {Feature} from "../model/feature";
// import 'underscore';

@Component({
  selector: 'sc-calendar-edit',
  templateUrl: './calendar-edit.component.html',
  styleUrls: ['./calendar-edit.component.scss']
})
export class CalendarEditComponent implements OnInit {

  originalCalendar: Calendar;
  calendar: Calendar;

  constructor(private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {

    this.route.data.subscribe((data: { calendar: Calendar }) => {
      this.originalCalendar = data.calendar.clone();
      this.calendar = data.calendar;
    });
  }

  addSeason() {
    this.calendar.seasons.push(new Season())
  }

  addFeature(season: Season) {
    season.features.push(new Feature())
  }

  deleteSeason(index: number) {
    this.calendar.seasons.splice(index);
  }

  deleteFeature(season: Season, index: number) {
    season.features.splice(index);
  }

  save() {

  }

  publish() {

  }
}
