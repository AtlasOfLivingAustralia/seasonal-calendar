import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import { Observable } from 'rxjs';
import {Calendar, ICalendar} from "./model/calendar";
import { environment } from "../environments/environment";
import {BieSearchResponse} from "./model/bie-search-response";

@Injectable({
  providedIn: 'root'
})
export class CalendarService {

  private readonly calendarsEndpoint : string = `${environment.api}calendars`;
  private readonly searchEndpoint : string = `${environment.api}search/bie`;
  private readonly imageEndpoint: string = `${environment.api}images/upload`;

  constructor(private httpClient: HttpClient) { }

  get calendars() : Observable<ICalendar[]> {
    return this.httpClient.get<Calendar[]>(this.calendarsEndpoint);
  }

  findCalendar(id: string) : Observable<ICalendar> {
    return this.httpClient.get<ICalendar>(`${this.calendarsEndpoint}/${id}`);
  }

  speciesSearch(query: string) : Observable<BieSearchResponse> {
    return this.httpClient.get<BieSearchResponse>(`${this.searchEndpoint}`, { params : {'q': query} })
  }

  newCalendar() {
    return new Calendar();
  }

  save(calendar: Calendar) {
    let endpoint = `${this.calendarsEndpoint}${calendar.collectionUuid ? `/${calendar.collectionUuid}` : ''}`;
    return this.httpClient.post(endpoint, this.sanitizeCalendarForUpload(calendar));
  }

  uploadImages(files/*: FileList*/): Observable<HttpEvent<Object>> {
    const formData = new FormData();

    for (let i = 0; i < files.length; i++) {
      formData.append("images", files[i], files[i].name);
    }

    return this.httpClient.post<string[]>(this.imageEndpoint, formData, {
      observe: 'events',
      reportProgress: true,
      responseType: 'json',
      withCredentials: true
    });
  }

  private sanitizeCalendarForUpload(calendar: ICalendar): ICalendar {
    return Calendar.fromJson(calendar)
  }
}
