import {Component, computed, input, model,} from '@angular/core';
import {FormValueControl, NgValidationError, ValidationError, WithOptionalField,} from '@angular/forms/signals';
import {TranslocoPipe} from '@jsverse/transloco';

/**
 * Generic text-like input component for Angular Signal Forms.
 *
 * This component is designed to be used with the `[formField]` directive
 * from `@angular/forms/signals`.
 *
 * Responsibilities:
 * - display a label when provided
 * - display a translated placeholder when provided
 * - bind the field value through the `FormValueControl` contract
 * - reflect UI state provided by Signal Forms (`disabled`, `required`, `invalid`, `errors`, etc.)
 * - render translated validation messages, including custom errors
 *
 * Notes:
 * - Validation rules must stay in the Signal Forms schema, not in this component.
 * - This component only renders the validation state it receives from `[formField]`.
 * - Translation keys are resolved through Transloco.
 */
@Component({
    selector: 'app-form-input',
    imports: [TranslocoPipe],
    templateUrl: './form-input.component.html'
})
export class FormInputComponent implements FormValueControl<string> {
    /**
     * Current field value bound by Signal Forms.
     * Must not be required at component creation time because `[formField]`
     * wires it after instantiation.
     */
    readonly value = model('');

    /**
     * Touched state.
     * Kept as a model signal so the component can mark itself as touched on blur.
     */
    readonly touched = model(false);

    /** HTML id used by both the label and the input. */
    readonly id = input.required<string>();

    /** Input type. */
    readonly type = input<'text' | 'email' | 'password'>('text');

    /** Optional translation key for the label displayed above the input. */
    readonly label = input<string | null>(null);

    /** Optional translation key for the placeholder displayed inside the input. */
    readonly placeholder = input<string | null>(null);

    /** Optional autocomplete attribute. */
    readonly autocomplete = input<string | null>(null);

    /** UI state injected by Signal Forms through `[formField]`. */
    readonly disabled = input(false);
    readonly readonly = input(false);
    readonly hidden = input(false);
    readonly required = input(false);
    readonly invalid = input(false);

    /**
     * Validation errors provided by Signal Forms.
     * Each error exposes a `kind` and may expose additional metadata depending on the validator.
     */
    readonly errors = input<readonly WithOptionalField<ValidationError>[]>([]);

    /** Validation constraints injected by Signal Forms. */
    readonly min = input<number | undefined>(undefined);
    readonly max = input<number | undefined>(undefined);
    readonly minLength = input<number | undefined>(undefined);
    readonly maxLength = input<number | undefined>(undefined);

    /**
     * Displays validation messages only after the field has been touched.
     */
    readonly showErrors = computed(() => this.touched() && this.invalid());

    /**
     * Returns the translation key associated with a validation error.
     * Falls back to `app.validation.<kind>` for unknown custom validators.
     */
    errorKey(error: ValidationError): string {
        return `validation.${error.kind}`;
    }

    /**
     * Returns the interpolation params expected by the translation key.
     *
     * For built-in Angular Signal Forms validation errors, some error kinds
     * expose typed metadata through `NgValidationError`.
     *
     * Examples:
     * - min       -> { min }
     * - max       -> { max }
     * - minLength -> { minLength }
     * - maxLength -> { maxLength }
     */
    errorParams(error: ValidationError): Record<string, unknown> {
        if (!(error instanceof NgValidationError)) {
            return {};
        }

        switch (error.kind) {
            case 'min':
                return {min: error.min};
            case 'max':
                return {max: error.max};
            case 'minLength':
                return {minLength: error.minLength};
            case 'maxLength':
                return {maxLength: error.maxLength};
            default:
                return {};
        }
    }
}
