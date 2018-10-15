import {Component, Input} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'sc-modal-confirm',
  template: `
<div class="modal-header">
  <h4 class="modal-title" id="modal-title">{{title}}</h4>
  <button type="button" class="close" aria-describedby="modal-title" (click)="modal.dismiss('Cross click')">
    <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <p>Are you sure you want to <strong>{{verb}}</strong> the <strong><span class="text-primary">{{name}}</span> {{type}}?</strong></p>
  <!--<p>All information associated to this {{type}} will be permanently deleted.-->
    <!--<span class="text-danger">This operation can not be undone.</span>-->
  <!--</p>-->
</div>
<div class="modal-footer">
  <button type="button" class="btn btn-outline-secondary" (click)="modal.dismiss('cancel click')">Cancel</button>
  <button type="button" class="btn btn-danger" (click)="modal.close('Ok click')">Ok</button>
</div>
`
})
export class ConfirmModalComponent {
  @Input() title: string = "Are you sure?";
  @Input() verb: string = "change";
  @Input() name: string = "the item";
  @Input() type: string = "calendar";

  constructor(public modal: NgbActiveModal) {}
}
