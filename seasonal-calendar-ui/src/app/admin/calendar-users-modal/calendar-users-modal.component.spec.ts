import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendarUsersModalComponent } from './calendar-users-modal.component';

describe('CalendarUsersModalComponent', () => {
  let component: CalendarUsersModalComponent;
  let fixture: ComponentFixture<CalendarUsersModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CalendarUsersModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarUsersModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
