import {TestBed} from '@angular/core/testing';
import {DashboardComponent} from './dashboard.component';
import {AuthService} from '../../core/services/auth.service';
import {of} from 'rxjs';
import {getTranslocoModule} from '../../transloco/testing/transloco-testing.module';
import {vi, describe, it, expect, beforeEach} from 'vitest';

describe('DashboardComponent', () => {
    let authService: any;

    beforeEach(async () => {
        authService = {
            currentUser$: of({username: 'dashboarduser', roles: []})
        };

        await TestBed.configureTestingModule({
            imports: [DashboardComponent, getTranslocoModule()],
            providers: [
                {provide: AuthService, useValue: authService}
            ]
        }).compileComponents();
    });

    it('should create', () => {
        const fixture = TestBed.createComponent(DashboardComponent);
        const component = fixture.componentInstance;
        expect(component).toBeTruthy();
    });

    it('should show username from signal', () => {
        const fixture = TestBed.createComponent(DashboardComponent);
        fixture.detectChanges();
        expect(fixture.componentInstance.username()).toBe('dashboarduser');
    });
});
