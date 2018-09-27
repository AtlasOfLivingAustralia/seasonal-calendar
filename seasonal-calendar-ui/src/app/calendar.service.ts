import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import { Observable } from 'rxjs';
import {Calendar, CalendarSaved, ICalendar} from "./model/calendar";
import { environment } from "../environments/environment";
import {BieSearchResponse} from "./model/bie-search-response";

@Injectable({
  providedIn: 'root'
})
export class CalendarService {

  private readonly calendarsEndpoint : string = `${environment.api}calendars`;
  private readonly searchEndpoint : string = `${environment.api}search/bie`;
  private readonly imageEndpoint: string = `${environment.api}images/upload`;
  private readonly languageGroupEndpoint: string = `${environment.api}language`;

  constructor(private httpClient: HttpClient) { }

  getCalendars(publishedOnly: boolean = true) : Observable<ICalendar[]> {
    return this.httpClient.get<Calendar[]>(this.calendarsEndpoint, { params: { publishedOnly: `${publishedOnly}`}});
  }

  findCalendar(id: string) : Observable<ICalendar> {
    return this.httpClient.get<ICalendar>(`${this.calendarsEndpoint}/${id}`);
  }

  findCalendarByLanguage(language: string) : Observable<ICalendar> {
    return this.httpClient.get<ICalendar>(`${this.languageGroupEndpoint}/${language}`);
  }

  speciesSearch(query: string) : Observable<BieSearchResponse> {
    return this.httpClient.get<BieSearchResponse>(`${this.searchEndpoint}`, { params : {'q': query} })
  }

  newCalendar() {
    return new Calendar();
  }

  save(calendar: Calendar) {
    let endpoint = `${this.calendarsEndpoint}${calendar.collectionUuid ? `/${calendar.collectionUuid}` : ''}`;
    return this.httpClient.post<CalendarSaved>(endpoint, this.sanitizeCalendarForUpload(calendar));
  }

  delete(calendar: ICalendar) {
    let endpoint = `${this.calendarsEndpoint}/${calendar.collectionUuid}`;
    return this.httpClient.delete(endpoint);
  }

  publish(calendar: ICalendar) {
    let endpoint = `${this.calendarsEndpoint}/${calendar.collectionUuid}/publish`;
    return this.httpClient.put(endpoint, {});
  }

  unpublish(calendar: ICalendar) {
    let endpoint = `${this.calendarsEndpoint}/${calendar.collectionUuid}/unpublish`;
    return this.httpClient.put(endpoint, {});
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
