import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProveedorDashboardComponent } from './proveedor-dashboard.component';

describe('ProveedorDashboardComponent', () => {
  let component: ProveedorDashboardComponent;
  let fixture: ComponentFixture<ProveedorDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProveedorDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProveedorDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
