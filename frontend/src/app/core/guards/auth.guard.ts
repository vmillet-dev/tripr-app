import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../services/auth.service';

export const AuthGuard: CanActivateFn = async (_route, state) => {
    const router = inject(Router);
    const authService = inject(AuthService);

    if (authService.isAuthenticated()) {
        return true;
    }

    await router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
    return false;
};
