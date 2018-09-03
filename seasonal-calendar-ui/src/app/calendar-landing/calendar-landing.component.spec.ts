import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendarLandingComponent } from './calendar-landing.component';

describe('CalendarLandingComponent', () => {
  let component: CalendarLandingComponent;
  let fixture: ComponentFixture<CalendarLandingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CalendarLandingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarLandingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
