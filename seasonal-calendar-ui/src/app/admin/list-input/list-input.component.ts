import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'sc-list-input',
  templateUrl: './list-input.component.html'
})
export class ListInputComponent implements OnInit {

  @Input('input-id') id: string = '';
  @Input() name: string = '';
  @Input() inputs: string[] = [];
  @Input() placeholder: string;
  @Output() inputsChanged = new EventEmitter<string[]>();

  newInput: string = '';

  constructor() { }

  ngOnInit() {
  }

  onKeydown($event: KeyboardEvent) {
    if ($event.keyCode == 9 && this.newInput != '') {
      this.updateInputs();
      $event.preventDefault();
    }
  }

  onKeypress($event: KeyboardEvent) {
    if ($event.key == 'Enter') {
      this.updateInputs();
      $event.preventDefault();
    }
  }

  onBlur($event) {
    this.updateInputs();
  }

  private updateInputs() {
    if (this.newInput.trim() != '') {
      this.inputs.push(this.newInput);
      this.inputsChanged.emit(this.inputs);
      this.newInput = '';
    }
  }

  deleteInput(index: number) {
    this.inputs.splice(index, 1);
    this.inputsChanged.emit(this.inputs);
  }

  trackByIndex(index, item) {
    return index;
  }
}
