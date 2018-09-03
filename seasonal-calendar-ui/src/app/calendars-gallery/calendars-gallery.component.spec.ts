import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendarsGalleryComponent } from './calendars-gallery.component';

describe('CalendarsGalleryComponent', () => {
  let component: CalendarsGalleryComponent;
  let fixture: ComponentFixture<CalendarsGalleryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CalendarsGalleryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarsGalleryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
