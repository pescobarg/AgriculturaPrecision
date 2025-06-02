import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgricultorDashboardComponent } from './agricultor-dashboard.component';

describe('AgricultorDashboardComponent', () => {
  let component: AgricultorDashboardComponent;
  let fixture: ComponentFixture<AgricultorDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgricultorDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgricultorDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
