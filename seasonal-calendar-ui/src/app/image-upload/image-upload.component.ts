import {Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';

@Component({
  selector: 'sc-image-upload',
  templateUrl: './image-upload.component.html',
  styleUrls: ['./image-upload.component.scss']
})
export class ImageUploadComponent {

  @Input() id: string;
  @Input() label: string = "Select file";
  @Input() class: string = "btn btn-primary";
  @Input() multiple: boolean = false;
  @Input() accept: string = '';
  @Input() public fileType: string[];
  @Output() select = new EventEmitter();

  @ViewChild('file') file: ElementRef;

  constructor(
  ) { }

  selectFile() {
    let ne = this.file.nativeElement;
    if (ne != null) {
      ne.click();
    }
  }

}
