import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import {NgbModal, NgbModalOptions} from "@ng-bootstrap/ng-bootstrap";
import {CalendarUsersModalComponent} from "../calendar-users-modal/calendar-users-modal.component";
import {Observable} from "rxjs";
import {ConfirmModalComponent} from "../confirm-modal/confirm-modal.component";
import {ICalendar} from "../../shared/model/calendar";
import {CalendarService, UserInfoService} from "../../shared/services/calendar.service";
import {MessageService} from "../../shared/services/message.service";
import {Logger} from "../../shared/services/logger.service";

@Component({
  selector: 'sc-admin-dashboard',
  templateUrl: './admin-dashboard.component.html'
})
export class AdminDashboardComponent implements OnInit {

  calendars: ICalendar[];
  userinfo$: Observable<any>;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private calendarService: CalendarService,
              private messageService: MessageService,
              private modalService: NgbModal,
              private userInfoService: UserInfoService,
              private log: Logger) { }

  ngOnInit() {
    this.route.data.subscribe((data: { calendars: ICalendar[] }) => {
      this.calendars = data.calendars;
    });
    this.userinfo$ = this.userInfoService.userinfo$
  }

  confirmPublish(calendar: ICalendar, publish: boolean) {
    const modalOptions: NgbModalOptions = {
      // size: 'sm'
    };
    let modalRef = this.modalService.open(ConfirmModalComponent, modalOptions);
    modalRef.componentInstance.name = calendar.name;
    modalRef.componentInstance.verb = publish ? "publish" : "unpublish";

    modalRef.result.then((result) => {
      if (publish) this.publish(calendar); else this.unpublish(calendar);
    }, (reason) => {
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

  confirmDelete(calendar: ICalendar) {
    const modalOptions: NgbModalOptions = {
      // size: 'sm'
    };
    let modalRef = this.modalService.open(ConfirmModalComponent, modalOptions);
    modalRef.componentInstance.name = calendar.name;
    modalRef.componentInstance.verb = "delete";

    modalRef.result.then((result) => {
      this.delete(calendar)
    }, (reason) => {
    });
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

  calendarUsersModal(calendar: ICalendar) {
    const modalOptions: NgbModalOptions = {
      // size: 'sm'
    };
    const modalRef = this.modalService.open(CalendarUsersModalComponent, modalOptions);
    modalRef.componentInstance.calendar = calendar;
    modalRef.componentInstance.calendarUsers = calendar.users;

    modalRef.result.then((result) => {
      if (result instanceof Array) {
        // feature.imageUrls = result;
        this.log.log("Got calendar users results:", result);
      } else {
        this.log.log("Got non array close result", result);
      }
    }, (reason) => {
    });
  }
}
