import {Component} from '@angular/core';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormSubmitDirective} from './form-submit.directive';

@Component({
    standalone: true,
    imports: [FormSubmitDirective],
    template: `
        <form #formSubmit="formSubmit">
            <button type="submit">Submit</button>
            <button type="button" (click)="formSubmit.reset()" id="reset-btn">Reset</button>
            @if (formSubmit.submitted()) {
                <div id="submitted-msg">Submitted</div>
            }
        </form>
    `
})
class TestComponent {
}

describe('FormSubmitDirective', () => {
    let fixture: ComponentFixture<TestComponent>;
    let directive: FormSubmitDirective;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TestComponent, FormSubmitDirective]
        }).compileComponents();

        fixture = TestBed.createComponent(TestComponent);
        fixture.detectChanges();

        const formEl = fixture.debugElement.query(By.directive(FormSubmitDirective));
        directive = formEl.injector.get(FormSubmitDirective);
    });

    it('should initialize submitted as false', () => {
        expect(directive.submitted()).toBe(false);
        expect(fixture.debugElement.query(By.css('#submitted-msg'))).toBeFalsy();
    });

    it('should set submitted to true on form submit', () => {
        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', null);
        fixture.detectChanges();

        expect(directive.submitted()).toBe(true);
        expect(fixture.debugElement.query(By.css('#submitted-msg'))).toBeTruthy();
    });

    it('should reset submitted to false when reset is called', () => {
        directive.submitted.set(true);
        fixture.detectChanges();

        const resetBtn = fixture.debugElement.query(By.css('#reset-btn'));
        resetBtn.triggerEventHandler('click', null);
        fixture.detectChanges();

        expect(directive.submitted()).toBe(false);
        expect(fixture.debugElement.query(By.css('#submitted-msg'))).toBeFalsy();
    });
});
