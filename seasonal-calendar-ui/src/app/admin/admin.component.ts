import { Component, OnInit } from '@angular/core';
import {ICalendar} from "../model/calendar";
import { ActivatedRoute, Router } from "@angular/router";

@Component({
  selector: 'sc-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  calendars: ICalendar[];

  constructor(private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {

    this.route.data.subscribe((data: { calendars: ICalendar[] }) => {
      this.calendars = data.calendars;
    });
  }

}
