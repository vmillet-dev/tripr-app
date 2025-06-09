import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { DashboardComponent } from './dashboard.component';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/auth.model';
import { getTranslocoModule } from '../../transloco/testing/transloco-testing.module';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockUser: User = {
    username: 'testuser',
    roles: ['ROLE_USER']
  };

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('AuthService', ['logout']);
    Object.defineProperty(spy, 'currentUser$', {
      value: of(mockUser),
      writable: true
    });

    await TestBed.configureTestingModule({
      imports: [DashboardComponent, getTranslocoModule()],
      providers: [
        { provide: AuthService, useValue: spy }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {


    expect(component).toBeTruthy();
  });

  it('should set username from current user on init', () => {

    component.ngOnInit();

    expect(component.username).toBe(mockUser.username);
  });

  it('should set username to null when no user', () => {
    Object.defineProperty(authServiceSpy, 'currentUser$', {
      value: of(null),
      writable: true
    });

    component.ngOnInit();

    expect(component.username).toBeNull();
  });

  it('should unsubscribe on destroy', () => {
    component.ngOnInit();
    spyOn(component['subscription'], 'unsubscribe');

    component.ngOnDestroy();

    expect(component['subscription'].unsubscribe).toHaveBeenCalled();
  });
});
