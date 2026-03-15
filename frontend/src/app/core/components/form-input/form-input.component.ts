import {ChangeDetectionStrategy, Component, computed, input, model,} from '@angular/core';
import {FormValueControl, NgValidationError, ValidationError, WithOptionalField,} from '@angular/forms/signals';
import {TranslocoPipe} from '@jsverse/transloco';

@Component({
    selector: 'app-form-input',
    standalone: true,
    imports: [TranslocoPipe],
    templateUrl: './form-input.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FormInputComponent implements FormValueControl<string> {
    // IMPORTANT : pas model.required() ici
    readonly value = model('');

    // touched peut être un ModelSignal si le contrôle le met à jour lui-même
    readonly touched = model(false);

    readonly id = input.required<string>();
    readonly type = input<'text' | 'email' | 'password'>('text');
    readonly label = input<string | null>(null);
    readonly placeholder = input<string | null>(null);
    readonly autocomplete = input<string | null>(null);

    readonly disabled = input(false);
    readonly readonly = input(false);
    readonly hidden = input(false);
    readonly required = input(false);
    readonly invalid = input(false);

    readonly errors = input<readonly WithOptionalField<ValidationError>[]>([]);

    readonly min = input<number | undefined>(undefined);
    readonly max = input<number | undefined>(undefined);
    readonly minLength = input<number | undefined>(undefined);
    readonly maxLength = input<number | undefined>(undefined);

    readonly showErrors = computed(() => this.touched() && this.invalid());

    errorKey(error: ValidationError): string {
        switch (error.kind) {
            case 'required':
                return 'validation.required';
            case 'email':
                return 'validation.email';
            case 'min':
                return 'validation.min';
            case 'max':
                return 'validation.max';
            case 'minLength':
                return 'validation.minLength';
            case 'maxLength':
                return 'validation.maxLength';
            case 'pattern':
                return 'validation.pattern';
            case 'passwordMismatch':
                return 'validation.passwordMismatch';
            default:
                return `validation.${error.kind}`;
        }
    }

    errorParams(error: ValidationError): Record<string, unknown> {
        if (error instanceof NgValidationError) {
            switch (error.kind) {
                case 'min':
                    return {min: error.min};
                case 'max':
                    return {max: error.max};
                case 'minLength':
                    return {minLength: error.minLength};
                case 'maxLength':
                    return {maxLength: error.maxLength};
            }
        }

        return {};
    }
}
