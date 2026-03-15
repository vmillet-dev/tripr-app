import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FormInputComponent} from './form-input.component';
import {getTranslocoModule} from "../../../transloco/testing/transloco-testing.module";

describe('FormInputComponent', () => {
    let component: FormInputComponent;
    let fixture: ComponentFixture<FormInputComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [FormInputComponent, getTranslocoModule()],
        })
            .compileComponents();

        fixture = TestBed.createComponent(FormInputComponent);
        component = fixture.componentInstance;
        fixture.componentRef.setInput('id', 'test-id');
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
