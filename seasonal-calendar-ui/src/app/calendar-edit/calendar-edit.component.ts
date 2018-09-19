import {Component, OnInit, ViewChild} from '@angular/core';
import {Calendar} from "../model/calendar";
import {ActivatedRoute, Router} from "@angular/router";
import {Season} from "../model/season";
import {Feature, IFeature} from "../model/feature";
import {CalendarService} from "../calendar.service";
import {Logger} from "../shared/logger.service";
import {NgbActiveModal, NgbModal, NgbModalConfig, NgbModalOptions, NgbTabset} from '@ng-bootstrap/ng-bootstrap';
import {ImageUploadModalComponent} from "../image-upload-modal/image-upload-modal.component";
import {Observable, of} from "rxjs";
import {catchError, debounceTime, distinctUntilChanged, map, switchMap, tap} from "rxjs/operators";

@Component({
  selector: 'sc-calendar-edit',
  templateUrl: './calendar-edit.component.html',
  styleUrls: ['./calendar-edit.component.scss']
})
export class CalendarEditComponent implements OnInit {

  originalCalendar: Calendar;
  calendar: Calendar;

  saving: boolean = false;

  @ViewChild(NgbTabset) tabset: NgbTabset;

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

  next() {
    let activeId = this.tabset.activeId;
    let tabIdx = this.tabset.tabs.reduce((prev, cur, idx, arr) => cur.id == activeId ? idx : prev, 0);

    let nextTabIdx =  (tabIdx + 1) % this.tabset.tabs.length;

    let nextTab = this.tabset.tabs.find((tab, i, arr) => i == nextTabIdx);
    this.tabset.select(nextTab.id);

  }

  addSeason() {
    this.calendar.seasons.push(new Season())
  }

  addFeature(season: Season) {
    season.features.push(new Feature())
  }

  deleteSeason(index: number) {
    this.calendar.seasons.splice(index,1);
  }

  deleteFeature(season: Season, index: number) {
    season.features.splice(index,1);
  }

  save() {
    this.saving = true;
    this.calendarService.save(this.calendar).subscribe(
      (value) => { },
      (error) => {
        this.log.error(error);

      }, () => {
        this.saving  = false;
      }
    );
  }

  publish() {

  }

  trackByKey(index, item) {
    item.getKey();
  }

}
