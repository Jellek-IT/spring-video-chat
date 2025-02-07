import { Component, DebugElement } from '@angular/core';
import { LetDirective } from './let.directive';
import { ComponentFixture, TestBed } from '@angular/core/testing';

@Component({
  template: ` <ng-container *appLet="value as v">{{ v }}</ng-container> `,
  standalone: true,
  imports: [LetDirective],
})
class TestComponent {
  value: string = 'Hello World';
}
describe('LetDirective', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestComponent],
    });
    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
  });

  it('should create an embedded view and bind value correctly', () => {
    const container = fixture.debugElement.nativeElement;
    expect(container.textContent.trim()).toBe('Hello World');
  });

  it('should update when input changes', () => {
    fixture.componentInstance.value = 'Updated Value';
    fixture.detectChanges();
    const container = fixture.debugElement.nativeElement;
    expect(container.textContent.trim()).toBe('Updated Value');
  });

  it('should not re-render if value does not change', () => {
    spyOn(fixture, 'detectChanges').and.callThrough();
    fixture.componentInstance.value = 'Hello World';
    fixture.detectChanges();
    expect(fixture.detectChanges).toHaveBeenCalledTimes(1);
  });
});
