import { Component, OnInit } from '@angular/core';
import {ICalendar} from "../model/calendar";
import { ActivatedRoute, Router } from "@angular/router";
import {CalendarService} from "../calendar.service";
import {MessageService} from "../messages/message.service";

@Component({
  selector: 'sc-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  calendars: ICalendar[];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private calendarService: CalendarService,
              private messageService: MessageService) { }

  ngOnInit() {
    this.route.data.subscribe((data: { calendars: ICalendar[] }) => {
      this.calendars = data.calendars;
    });
  }

  publish(calendar: ICalendar) {
    this.calendarService.publish(calendar).subscribe(
      (value) => {
        calendar.published = true;
        this.messageService.add({
          text: `${calendar.name} published`,
          timeout: 5000
        })
      },
      (error) => {
        this.messageService.add({
          text: `An error occured publishing ${calendar.name}`,
          action: {
            text: "Dismiss"
          }
        })
      }
    );
  }

  unpublish(calendar: ICalendar) {
    this.calendarService.unpublish(calendar).subscribe(
      (value) => {
        calendar.published = false;
        this.messageService.add({
          text: `${calendar.name} unpublished`,
          timeout: 5000
        })
      },
      (error) => {
        this.messageService.add({
          text: `An error occured unpublishing ${calendar.name}`,
          action: {
            text: "Dismiss"
          }
        })
      }
    );
  }

  delete(calendar: ICalendar) {
    this.calendarService.delete(calendar).subscribe(
      (value) => {
        this.calendars.splice(this.calendars.indexOf(calendar), 1);
        this.messageService.add({
          text: `${calendar.name} deleted`,
          timeout: 5000
        })
      },
      (error) => {
        this.messageService.add({
          text: `An error occured deleting ${calendar.name}`,
          action: {
            text: "Dismiss"
          }
        })
      }
    );
  }
}
