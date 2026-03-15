import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DashboardComponent} from './dashboard.component';
import {AuthService} from '../../core/services/auth.service';
import {getTranslocoModule} from '../../transloco/testing/transloco-testing.module';
import {BehaviorSubject} from 'rxjs';
import {provideZonelessChangeDetection} from '@angular/core';

describe('DashboardComponent', () => {
    let component: DashboardComponent;
    let fixture: ComponentFixture<DashboardComponent>;
    let currentUserSubject: BehaviorSubject<any>;

    beforeEach(async () => {
        currentUserSubject = new BehaviorSubject<any>(null);

        await TestBed.configureTestingModule({
            imports: [DashboardComponent, getTranslocoModule()],
            providers: [
                provideZonelessChangeDetection(),
                {
                    provide: AuthService,
                    useValue: {currentUser$: currentUserSubject.asObservable()}
                }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(DashboardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should display username from authService', () => {
        currentUserSubject.next({username: 'dashboard-user'});
        fixture.detectChanges();
        expect(component.username()).toBe('dashboard-user');
    });

    it('should return null if no user', () => {
        currentUserSubject.next(null);
        fixture.detectChanges();
        expect(component.username()).toBeNull();
    });
});
