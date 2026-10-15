import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NewMemberForm } from './lead-forms';

describe('Forms', () => {
  let component: NewMemberForm;
  let fixture: ComponentFixture<NewMemberForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewMemberForm],
    }).compileComponents();

    fixture = TestBed.createComponent(NewMemberForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
