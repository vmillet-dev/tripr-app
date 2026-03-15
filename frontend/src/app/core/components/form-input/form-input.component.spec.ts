import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FormInputComponent} from './form-input.component';
import {getTranslocoModule} from "../../../transloco/testing/transloco-testing.module";
import {By} from '@angular/platform-browser';
import {FormSubmitDirective} from '../../directives/form-submit.directive';
import {Component, viewChild} from '@angular/core';

@Component({
    standalone: true,
    imports: [FormInputComponent, FormSubmitDirective],
    template: `
        <form #form="formSubmit">
            <app-form-input id="test-input" [label]="label" [placeholder]="placeholder" [invalid]="isInvalid"
                            [errors]="errors"
                            [touched]="isTouched"></app-form-input>
        </form>
    `
})
class TestHostComponent {
    label: string | null = 'Test Label';
    placeholder: string | null = null;
    isInvalid = false;
    isTouched = false;
    errors: any[] = [];
    inputComponent = viewChild(FormInputComponent);
    formDirective = viewChild(FormSubmitDirective);
}

describe('FormInputComponent', () => {
    let hostComponent: TestHostComponent;
    let fixture: ComponentFixture<TestHostComponent>;
    let component: FormInputComponent;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TestHostComponent, FormInputComponent, getTranslocoModule()],
        }).compileComponents();

        fixture = TestBed.createComponent(TestHostComponent);
        hostComponent = fixture.componentInstance;
        fixture.detectChanges();
        component = hostComponent.inputComponent()!;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it.skip('should display label and placeholder when provided', async () => {
        hostComponent.label = 'app.test.label';
        hostComponent.placeholder = 'app.test.placeholder';
        await fixture.whenStable();
        fixture.detectChanges();

        const label = fixture.debugElement.query(By.css('label'));
        const input = fixture.debugElement.query(By.css('input'));

        expect(label.nativeElement.textContent).toContain('app.test.label');
        expect(input.nativeElement.placeholder).toBe('app.test.placeholder');
    });

    describe('Validation errors display (showErrors)', () => {
        it.skip('should NOT show errors initially', async () => {
            hostComponent.isInvalid = true;
            await fixture.whenStable();
            fixture.detectChanges();
            expect(component.showErrors()).toBe(false);
        });

        it.skip('should show errors when invalid and touched', async () => {
            hostComponent.isInvalid = true;
            hostComponent.isTouched = true;
            await fixture.whenStable();
            fixture.detectChanges();
            expect(component.showErrors()).toBe(true);
        });

        it.skip('should show errors when invalid and form is submitted', async () => {
            hostComponent.isInvalid = true;
            await fixture.whenStable();
            fixture.detectChanges();
            hostComponent.formDirective()?.submitted.set(true);
            fixture.detectChanges();
            expect(component.showErrors()).toBe(true);
        });
    });

    describe('errorKey and errorParams', () => {
        it('should return correct error key', () => {
            const error = {kind: 'required'} as any;
            expect(component.errorKey(error)).toBe('validation.required');
        });

        it('should return params from error if present', () => {
            const errorWithParams = {kind: 'custom', params: {foo: 'bar'}} as any;
            expect(component.errorParams(errorWithParams)).toEqual({foo: 'bar'});
        });

        it('should return correct params for built-in error keys', () => {
            const minError = {kind: 'min', min: 10} as any;
            expect(component.errorParams(minError)).toEqual({min: 10});

            const maxError = {kind: 'max', max: 100} as any;
            expect(component.errorParams(maxError)).toEqual({max: 100});

            const minLengthError = {kind: 'minLength', minLength: 6} as any;
            expect(component.errorParams(minLengthError)).toEqual({minLength: 6});

            const maxLengthError = {kind: 'maxLength', maxLength: 20} as any;
            expect(component.errorParams(maxLengthError)).toEqual({maxLength: 20});
        });
    });
});
