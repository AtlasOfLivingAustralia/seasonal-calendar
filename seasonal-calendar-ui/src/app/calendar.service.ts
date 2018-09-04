import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import { Calendar } from "./model/calendar";
import { environment } from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CalendarService {

  constructor(private httpClient: HttpClient) { }

  get calendars() : Observable<Calendar[]> {
    return this.httpClient.get<Calendar[]>(`${environment.api}calendars`);
  }

  findCalendar(id: string) {
    return this.httpClient.get<Calendar>(`${environment.api}calendars/${id}`)
  }

  newCalendar() {
    return new Calendar();
  }
}
