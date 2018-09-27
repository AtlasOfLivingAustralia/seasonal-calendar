import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageGroupComponent } from './language-group.component';

describe('LanguageGroupComponent', () => {
  let component: LanguageGroupComponent;
  let fixture: ComponentFixture<LanguageGroupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LanguageGroupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
