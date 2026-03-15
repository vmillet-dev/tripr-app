import {Directive, HostListener, signal} from '@angular/core';

/**
 * Directive to track the submission status of a form.
 * Can be injected by child components to react to the submitted state.
 */
@Directive({
    selector: 'form',
    standalone: true,
    exportAs: 'formSubmit'
})
export class FormSubmitDirective {
    readonly submitted = signal(false);

    @HostListener('submit')
    onSubmit(): void {
        this.submitted.set(true);
    }

    reset(): void {
        this.submitted.set(false);
    }
}
