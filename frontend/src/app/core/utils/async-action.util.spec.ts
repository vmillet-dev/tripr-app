import {createAsyncAction} from './async-action.util';
import {of} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {vi} from 'vitest';

describe('createAsyncAction', () => {
    it('should initialize with default states', () => {
        const action = createAsyncAction((arg: string) => of(arg));
        expect(action.loading()).toBe(false);
        expect(action.error()).toBeNull();
        expect(action.data()).toBeNull();
        expect(action.success()).toBe(false);
        expect(action.isIdle()).toBe(true);
    });

    it('should set loading and reset states on execute', () => {
        const action = createAsyncAction((arg: string) => of(arg));
        action.execute('test');
        expect(action.loading()).toBe(true);
        expect(action.error()).toBeNull();
        expect(action.success()).toBe(false);
        expect(action.isIdle()).toBe(false);
    });

    it('should handle success', () => {
        const onSuccess = vi.fn();
        const action = createAsyncAction((arg: string) => of(arg), {onSuccess});

        action.execute('test').subscribe(res => {
            action.handleSuccess(res);
        });

        expect(action.loading()).toBe(false);
        expect(action.success()).toBe(true);
        expect(action.data()).toBe('test');
        expect(onSuccess).toHaveBeenCalledWith('test');
    });

    it('should handle error from HttpErrorResponse', () => {
        const onError = vi.fn();
        const action = createAsyncAction((arg: string) => of(arg), {onError});

        const errorResponse = new HttpErrorResponse({
            error: {error: 'SERVER_ERROR'},
            status: 500
        });

        action.handleError(errorResponse, {id: 123});

        expect(action.loading()).toBe(false);
        expect(action.error()).toEqual({
            message: 'errors.SERVER_ERROR',
            params: {id: 123}
        });
        expect(onError).toHaveBeenCalledWith(errorResponse);
    });

    it('should handle generic error', () => {
        const action = createAsyncAction((arg: string) => of(arg));
        action.handleError(new Error('Generic error'));

        expect(action.error()?.message).toBe('errors.DEFAULT');
    });

    it('should handle HttpErrorResponse with missing error property', () => {
        const action = createAsyncAction((arg: string) => of(arg));
        const errorResponse = new HttpErrorResponse({status: 404});
        action.handleError(errorResponse);

        expect(action.error()?.message).toBe('errors.DEFAULT');
    });
});
