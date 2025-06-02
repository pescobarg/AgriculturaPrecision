import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpertoDashboardComponent } from './experto-dashboard.component';

describe('ExpertoDashboardComponent', () => {
  let component: ExpertoDashboardComponent;
  let fixture: ComponentFixture<ExpertoDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpertoDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpertoDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
