import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MessageService} from './message.service';
import {Uuid} from "../shared/uuid";

@Component({
  selector: 'sc-messagebar',
  template: `
    <div class="messagebar" [ngClass]="position || 'bottom-right'">
      <sc-message *ngFor="let message of messages"
                  [text]="message.text"
                  [background]="message.background || background"
                  [extraClass]="message.extraClass || extraMessageClass"
                  [color]="message.color || color || calcTextColor(message.background || background)"
                  [actionText]="message.action?.text"
                  (action)="message.action?.onClick()"
      >
      </sc-message>
    </div>
  `,
  styleUrls: ['message-bar.component.scss'],
  styles: [`
    .messagebar {
      position: fixed;
      z-index: 99999;
      display: flex;
      flex-direction: column;
    }

    .messagebar.bottom-left {
      left: 2rem;
      bottom: 1.5rem;
      align-items: flex-start;
    }

    .messagebar.bottom-center {
      left: 50%;
      transform: translate(-50%, 0);
      bottom: 1.5rem;
      align-items: center;
    }

    .messagebar.bottom-right {
      right: 2rem;
      bottom: 1.5rem;
      align-items: flex-end;
    }

    .messagebar.top-left {
      left: 2rem;
      top: 2rem;
      align-items: flex-start;
    }

    .messagebar.top-center {
      left: 50%;
      transform: translate(-50%, 0);
      top: 2rem;
      align-items: center;
    }

    .messagebar.top-right {
      right: 2rem;
      top: 2rem;
      align-items: flex-end;
    }
  `]
})
export class MessageBarComponent {
  @Input() position: 'top-left' | 'top-center' | 'top-right' | 'bottom-left' | 'bottom-center' | 'bottom-right';
  @Input() max: number;
  @Input() background: string;
  @Input() accent: string;
  @Input() color: string;
  @Input() extraMessageClass: any;
  @Input() timeout: number;

  @Output() public onAdd: EventEmitter<any> = new EventEmitter<any>();
  @Output() public onRemove: EventEmitter<any> = new EventEmitter<any>();
  @Output() public onClear: EventEmitter<boolean> = new EventEmitter<boolean>();

  messages: Array<{
    id: string, text: string, timeout?: number, color?: string, background?: string, extraClass?: any, action?: {
      text: string, onClick?: Function, color?: string
    }, onAdd?: Function, onRemove?: Function, timeoutObj?: any
  }> = [];

  constructor(private messageService: MessageService) {
    this.messageService.get()
      .subscribe(message => {
        if (message.action === 'add') {
          this.add(message.data);
        } else if (message.action === 'remove') {
          this.remove(message.id);
        } else if (message.action === 'clear') {
          this.clear();
        }
      });
  }

  add(message) {
    let timeout;
    const id = Uuid.newUuid();

    if (this.max && this.max > 0 && this.messages.length === this.max) {
      this.remove(this.messages[0].id);
    }

    if (message.timeout || this.timeout) {
      timeout = setTimeout(() => {
        this.remove(id);
      }, message.timeout || this.timeout)
    }

    const data = Object.assign({id: id, timeoutObj: timeout}, message);

    if (message.action) {
      // ensure that the action closes the message
      const that = this;
      const msgFn = message.action.onClick || new Function();
      message.action.onClick = () => {
        msgFn(data);
        that.remove(id);
      };
    }

    if (message.onAdd) {
      message.onAdd(data);
    }

    if (this.onAdd.observers.length > 0) {
      this.onAdd.emit(data);
    }

    this.messages.push(data);
  }

  remove(id) {
    const message = this.messages.find(obj => obj.id === id);

    if (message) {
      if (message.onRemove) {
        message.onRemove(message);
      }

      if (this.onRemove.observers.length > 0) {
        this.onRemove.emit(message);
      }

      if (message.timeoutObj) {
        clearTimeout(message.timeoutObj);
      }
    }

    this.messages = this.messages.filter(obj => obj.id !== id);
  }

  clear() {
    this.messages = [];

    if (this.onClear.observers.length > 0) {
      this.onClear.emit(true);
    }
  }

  calcTextColor(background) {
    if (!background) {
      return null;
    }

    function hexToRgb(hex) {
      const shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
      hex = hex.replace(shorthandRegex, (m, r, g, b) => {
        return r + r + g + g + b + b;
      });

      const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
      return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
      } : null;
    }

    const rgb = hexToRgb(background);
    if (!rgb) {
      return null;
    }

    const color = [rgb.r / 255, rgb.g / 255, rgb.b / 255];

    for (let i = 0; i < color.length; ++i) {
      if (color[i] <= 0.03928) {
        color[i] = color[i] / 12.92;
      } else {
        color[i] = Math.pow((color[i] + 0.055) / 1.055, 2.4);
      }
    }

    const l = 0.2126 * color[0] + 0.7152 * color[1] + 0.0722 * color[2];

    if (l > 0.179) {
      return '#000';
    } else {
      return '#fff';
    }
  }
}
