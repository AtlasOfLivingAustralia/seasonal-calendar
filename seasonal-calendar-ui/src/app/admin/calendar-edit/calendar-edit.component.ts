import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {NgbTabset} from '@ng-bootstrap/ng-bootstrap';
import {CalendarService} from "../../shared/services/calendar.service";
import {Logger} from "../../shared/services/logger.service";
import {MessageService} from "../../shared/services/message.service";
import {Calendar} from "../../shared/model/calendar";
import {Season} from "../../shared/model/season";
import {Feature} from "../../shared/model/feature";

@Component({
  selector: 'sc-calendar-edit',
  templateUrl: './calendar-edit.component.html'
})
export class CalendarEditComponent implements OnInit {

  originalCalendar: Calendar;
  calendar: Calendar;

  saving: boolean = false;

  @ViewChild(NgbTabset) tabset: NgbTabset;
  @ViewChild('calendarForm') form;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private calendarService: CalendarService,
              private log: Logger,
              private messageService: MessageService) { }

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
    let uuid = this.calendar.collectionUuid;
    this.calendarService.save(this.calendar).subscribe(
      (value) => {
        if (!uuid) {
          let newUuid = value.collectionUuid;
          this.messageService.add({text: `${this.calendar.name} created`, timeout: 5000});
          this.router.navigate(['admin', 'calendar', newUuid]);
        } else {
          this.messageService.add({text: `${this.calendar.name} saved`, timeout: 5000});
        }
      },
      (error) => {
        this.messageService.add({text: "An error occured saving the calendar", action: {text:"Dismiss"}});
        this.log.error(error);

      }, () => {
        this.saving  = false;
      }
    );
  }

  trackByKey(index, item) {
    item.getKey();
  }

}
