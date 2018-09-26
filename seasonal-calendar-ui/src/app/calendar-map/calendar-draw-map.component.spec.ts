import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendarDrawMapComponent } from './calendar-draw-map.component';

describe('CalendarDrawMapComponent', () => {
  let component: CalendarDrawMapComponent;
  let fixture: ComponentFixture<CalendarDrawMapComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CalendarDrawMapComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarDrawMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
