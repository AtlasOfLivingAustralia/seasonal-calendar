import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureEditComponent } from './feature.component';

describe('FeatureEditComponent', () => {
  let component: FeatureEditComponent;
  let fixture: ComponentFixture<FeatureEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FeatureEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
