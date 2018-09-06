import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import { Observable } from 'rxjs';
import { Calendar } from "./model/calendar";
import { environment } from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CalendarService {

  private readonly calendarsEndpoint : string = `${environment.api}calendars`;
  private readonly imageEndpoint: string = `${environment.api}images`;

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

  uploadImages(files/*: FileList*/): Observable<HttpEvent<Object>> {
    const formData = new FormData();

    for (let i = 0; i < files.length; i++) {
      formData.append(i.toString(), files[i], files[i].name);
    }

    return this.httpClient.post(this.imageEndpoint, formData, {
      observe: 'events',
      reportProgress: true,
      responseType: 'json',
      withCredentials: true
    });
  }
}
