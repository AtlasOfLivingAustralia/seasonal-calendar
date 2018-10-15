import {Component, Input} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Observable, of} from "rxjs";
import {catchError, debounceTime, distinctUntilChanged, map, switchMap, tap} from "rxjs/operators";
import {ICalendar} from "../../shared/model/calendar";
import {Logger} from "../../shared/services/logger.service";
import {CalendarService} from "../../shared/services/calendar.service";
import {MessageService} from "../../shared/services/message.service";
import {UserDetails} from "../../shared/model/bie-search-response";

@Component({
  selector: 'sc-calendar-users-modal',
  templateUrl: './calendar-users-modal.component.html'
})
export class CalendarUsersModalComponent {

  @Input() calendar: ICalendar;

  public searching: boolean = false;
  public searchFailed: boolean = false;
  public calendarUsers: Array<{username: string, admin: boolean}> = [];

  constructor(
    public activeModal: NgbActiveModal,
    private log: Logger,
    private calendarService: CalendarService,
    private messageService: MessageService
  ) { }

  private _selectedResponse: UserDetails;
  get selectedResponse(): UserDetails{
    return this._selectedResponse;
  };
  set selectedResponse(selectedResponse: UserDetails) {
    this._selectedResponse = selectedResponse;
    if (selectedResponse) {
      if (!this.calendarUsers) this.calendarUsers = [];
      this.calendarUsers.push({username: selectedResponse.email, admin: false});
    }
  }

  searchInputFormatter = (result: UserDetails) => {
    return `${result.firstName} ${result.lastName} (${result.email})`
  };

  search = (text$: Observable<string>) => {
    return text$.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
      tap(() => this.searching = true),
      switchMap(term =>
        this.calendarService.userSearch(term).pipe(
          tap(() => this.searchFailed = false),
          catchError(() => {
            this.searchFailed = true;
            return of([]);
          }))
      ),
      tap(() => this.searching = false)
    )
  };

  remove(idx) {
    this.calendarUsers.splice(idx, 1);
  }

  saving: boolean = false;

  save() {
    this.saving = true;
    this.calendarService.saveUsers(this.calendar, this.calendarUsers).pipe(
      map(() => true),
      catchError(() => of(false)),
    ).subscribe(success => {
        this.saving = false;
        if (success) {
          this.activeModal.close(this.calendarUsers);
          this.messageService.add({
            text: 'Permissions saved',
            timeout: 5000
          })
        } else {
          this.messageService.add({
            text: 'Saving permissions failed',
            action: {
              text: 'Dismiss'
            }
          });
        }
      }
    );
  }

  cancel() {
    this.activeModal.dismiss("cancel");
  }

}
