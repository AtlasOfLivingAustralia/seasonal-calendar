import { Component, OnInit } from '@angular/core';
import {Calendar} from "../model/calendar";
import {ActivatedRoute, Router} from "@angular/router";
import {Season} from "../model/season";
import {Feature} from "../model/feature";
import {CalendarService} from "../calendar.service";
import {Logger} from "../shared/logger.service";

@Component({
  selector: 'sc-calendar-edit',
  templateUrl: './calendar-edit.component.html',
  styleUrls: ['./calendar-edit.component.scss']
})
export class CalendarEditComponent implements OnInit {

  originalCalendar: Calendar;
  calendar: Calendar;

  saving: boolean = false;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private calendarService: CalendarService,
              private log: Logger) { }

  ngOnInit() {

    this.route.data.subscribe((data: { calendar: Calendar }) => {
      this.originalCalendar = data.calendar.clone();
      this.calendar = data.calendar;
    });
  }

  reset() {
    this.calendar = this.originalCalendar.clone();
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
    this.saving = true;
    this.calendarService.save(this.calendar).subscribe(
      (value) => { this.saving = false; },
      (error) => this.log.error(error)
    );
  }

  publish() {

  }

  trackByKey(index, item) {
    item.getKey();
  }
}
