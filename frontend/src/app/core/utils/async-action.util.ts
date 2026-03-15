import {computed, signal} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';

export interface AsyncError {
    message: string;
    params?: any;
}

export interface AsyncActionState<R> {
    loading: () => boolean;
    error: () => AsyncError | null;
    data: () => R | null;
    success: () => boolean;
    isIdle: () => boolean;
}

export interface AsyncActionOptions<R> {
    onSuccess?: (res: R) => void;
    onError?: (err: any) => void;
    defaultErrorMessage?: string;
}

/**
 * Utility to manage the state of an asynchronous action (mutation/form submission).
 * Encapsulates loading, error, success and data states into signals.
 */
export function createAsyncAction<T, R>(
    actionFn: (args: T) => Observable<R>,
    options?: AsyncActionOptions<R>
) {
    const loading = signal(false);
    const error = signal<AsyncError | null>(null);
    const data = signal<R | null>(null);
    const success = signal(false);

    const isIdle = computed(() => !loading() && !error() && !success());

    const execute = (args: T) => {
        loading.set(true);
        error.set(null);
        success.set(false);

        return actionFn(args);
    };

    const handleSuccess = (res: R) => {
        data.set(res);
        loading.set(false);
        success.set(true);
        options?.onSuccess?.(res);
    };

    const handleError = (err: any, params?: any) => {
        const message = err instanceof HttpErrorResponse ? err.error?.error : 'DEFAULT'

        error.set({
            message: 'errors.' + message,
            params
        });
        loading.set(false);
        options?.onError?.(err);
    };

    return {
        loading,
        error,
        data,
        success,
        isIdle,
        execute,
        handleSuccess,
        handleError,
    };
}
