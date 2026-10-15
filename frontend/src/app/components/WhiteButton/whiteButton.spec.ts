import { ComponentFixture, TestBed } from '@angular/core/testing';
import { whiteButton } from './whiteButton';

describe('Button', () => {
  let component: whiteButton;
  let fixture: ComponentFixture<whiteButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [whiteButton],
    }).compileComponents();

    fixture = TestBed.createComponent(whiteButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
