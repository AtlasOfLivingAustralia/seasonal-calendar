import {Component, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'sc-message',
  template: `
    <div class="message py-3 pl-4 mb-2 rounded" [style.background]="background" [style.color]="color" [ngClass]="extraClass">
      <div class="message-text mr-4">{{text}}</div>
      <div *ngIf="actionText" class="dismiss-action mr-4" (click)="action.emit()"
           [style.color]="actionColour">
        {{actionText}}
      </div>
    </div>
  `,
  styleUrls: ['message.component.scss']
})
export class MessageComponent {
  @Input() background;
  @Input() color;
  @Input() extraClass;
  @Input() text;
  @Input() actionText;
  @Input() actionColour;
  @Output() action = new EventEmitter();
}
