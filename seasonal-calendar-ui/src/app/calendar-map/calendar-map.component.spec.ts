import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendarMapComponent } from './calendar-map.component';

describe('CalendarMapComponent', () => {
  let component: CalendarMapComponent;
  let fixture: ComponentFixture<CalendarMapComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CalendarMapComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
