import { Component, OnInit } from '@angular/core';
import {Calendar} from "../model/calendar";
import {ActivatedRoute, Router} from "@angular/router";
import {Season} from "../model/season";
import {Feature, IFeature} from "../model/feature";
import {CalendarService} from "../calendar.service";
import {Logger} from "../shared/logger.service";
import {NgbActiveModal, NgbModal, NgbModalConfig, NgbModalOptions} from '@ng-bootstrap/ng-bootstrap';
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

  constructor(private route: ActivatedRoute,
              private router: Router,
              private calendarService: CalendarService,
              private log: Logger,
              private modalService: NgbModal) { }

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
    this.calendar.seasons.splice(index,1);
  }

  deleteFeature(season: Season, index: number) {
    season.features.splice(index,1);
  }

  save() {
    this.saving = true;
    this.calendarService.save(this.calendar).subscribe(
      (value) => { this.saving = false; },
      (error) => this.log.error(error)
    );
  }

  next() {

  }

  publish() {

  }

  trackByKey(index, item) {
    item.getKey();
  }

  trackByIndex(index, item) {
    return index;
  }

  imageUploadModal(feature: IFeature) {
    const modalOptions: NgbModalOptions = {
      size: 'lg'
    };
    const modalRef = this.modalService.open(ImageUploadModalComponent, modalOptions);
    modalRef.componentInstance.title = `Upload images for ${feature.name}`;
    modalRef.componentInstance.label = "Select feature images";
    modalRef.componentInstance.imageUrls = feature.imageUrls;

    modalRef.result.then((result) => {
      if (result instanceof Array) {
        feature.imageUrls = result;
      } else {
        this.log.log("Got non array close result", result);
      }
    }, (reason) => {
    });
  }

  searching = false;
  searchFailed = false;

  search = (text$: Observable<string>) => {
    return text$.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
      tap(() => this.searching = true),
      switchMap(term =>
        this.calendarService.speciesSearch(term).pipe(
          tap(() => this.searchFailed = false),
          map( ((value, index) => value.autoCompleteList
            .filter((val, idx, arr) => val.name != null && val.name != '')
            .map((val, idx, arr) => val.name))),
          catchError(() => {
            this.searchFailed = true;
            return of([]);
          }))
      ),
      tap(() => this.searching = false)
    )
  }
}
