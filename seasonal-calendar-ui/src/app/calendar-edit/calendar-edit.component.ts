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

  uploadingImages: any[] = [];

  calendarImageChanged($event) {
    let files: FileList = $event.target.files;
    for (let i = 0; i < files.length; ++i) {
      let file = files.item(i);
      this.startUploadingImage(file);
      // file.
    }
    this.log.log($event);
  }

  private startUploadingImage(file: File) {
    if (file) {
      let uploading = {
        filename: file.name,
        size: file.size,
        url: null
      };
      this.uploadingImages.push(uploading);
      this.ensureSize(file, 64, 64, (url) => uploading.url = url);
      // let reader = new FileReader();
      // reader.addEventListener("load", () => {
      //   this.uploadingImages.push(this.ensureSize(reader.result, 64, 64));
      // }, false);
      // reader.readAsArrayBuffer(file);
    }
  }

  private ensureSize(file, width: number, height: number, callback) {
    // create an off-screen canvas
    const canvas = document.createElement('canvas'),
      ctx = canvas.getContext('2d');


    let img = new Image;
    let url = URL.createObjectURL(file);
    img.addEventListener("load", () => {
      ctx.drawImage(img, 0, 0, width, height);

      callback(canvas.toDataURL());
      URL.revokeObjectURL(url);
    });

    img.src = url;
    // set its dimension to target size
    canvas.width = width;
    canvas.height = height;

  }
}
