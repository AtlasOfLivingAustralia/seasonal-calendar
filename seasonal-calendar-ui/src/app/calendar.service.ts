import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import { Calendar } from "./model/calendar";
import { environment } from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CalendarService {

  private readonly calendarsEndpoint : string = `${environment.api}calendars`;

  constructor(private httpClient: HttpClient) { }

  get calendars() : Observable<Calendar[]> {
    return this.httpClient.get<Calendar[]>(this.calendarsEndpoint);
  }

  findCalendar(id: string) {
    return this.httpClient.get<Calendar>(`${this.calendarsEndpoint}/${id}`);
  }

  newCalendar() {
    return new Calendar();
  }

  save(calendar: Calendar) {
    let endpoint = `${this.calendarsEndpoint}${calendar.collectionUuid ? `/${calendar.collectionUuid}` : ''}`;
    return this.httpClient.post(endpoint, calendar);
  }
}
