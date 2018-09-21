import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';

@Injectable()
export class MessageService {
  private messageService = new Subject<any>();

  get() {
    return this.messageService.asObservable();
  }

  add(data: {
    text: string, timeout?: number, background?: string, color?: string, customClass?: any,
    action?: { text: string, onClick?: Function, color?: string }, onAdd?: Function, onRemove?: Function
  }) {
    this.messageService.next({
      action: 'add',
      data: data
    });
  }

  remove(id: string) {
    this.messageService.next({action: 'remove', id: id});
  }

  clear() {
    this.messageService.next({action: 'clear'});
  }
}
