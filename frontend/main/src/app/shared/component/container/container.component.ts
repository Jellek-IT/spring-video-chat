import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
	selector: 'app-container',
	imports: [CommonModule],
	templateUrl: './container.component.html',
	styleUrl: './container.component.scss'
})
export class ContainerComponent {
	@Input() public type: 'small' | 'normal' | 'big' = 'normal';
}
