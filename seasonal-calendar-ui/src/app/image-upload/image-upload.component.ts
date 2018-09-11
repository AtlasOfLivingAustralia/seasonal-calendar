import {Component, ElementRef, EventEmitter, Input, OnInit, Output, Renderer2, ViewChild} from '@angular/core';
import {CalendarService} from "../calendar.service";
import {Upload} from "../model/image-upload";
import {Logger} from "../shared/logger.service";
import {HttpEventType} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Subscription} from "rxjs";

@Component({
  selector: 'sc-image-upload',
  templateUrl: './image-upload.component.html',
  styleUrls: ['./image-upload.component.scss']
})
export class ImageUploadComponent implements OnInit {

  @Input() label: string = "Select image";
  @Input() class: string = "btn btn-outline-primary";
  @Input() accept: string = "image/*";

  @Input() imageUrl: string;
  @Output() imageUrlChange = new EventEmitter<string>();

  @ViewChild('file') file: ElementRef;
  private _dropZone: ElementRef;

  @ViewChild('dropZone')  set dropZone(dropZone: ElementRef) {
    this._dropZone = dropZone;
  }

  private readonly imageEndpoint: string = `${environment.api}images/`;

  upload ?: Upload;

  uploadSubscription ?: Subscription;

  constructor(
    private calendarService: CalendarService,
    private log: Logger
  ) { }


  ngOnInit() {
  }

  selectFile() {
    let ne = this.file.nativeElement;
    if (ne != null) {
      ne.click();
    }
  }

  imageChanged($event) {
    let files: FileList = $event.target.files;
    this.log.log($event);
    if (files.length > 0) {
      this.startUploadingImage(files[0]);
    } else {

    }
  }

  private startUploadingImage(file: File) {
    if (file) {
      this.upload = {
        filename: file.name,
        size: file.size,
        url: null,
        uploaded: 0
      };
      // this.loadPreview(file, (dataUrl) => this.upload.url = dataUrl);
      // this.ensureSize(file, 527, 297, (url) => this.upload.url = url);
      this.ensureWidth(file, 527, (url) => this.upload.url = url);
      this.uploadSubscription = this.calendarService.uploadImages([file]).subscribe(
        (next) => {
          if (next.type == HttpEventType.UploadProgress) {
            this.log.log("Image upload progress", next);
            this.upload.uploaded = next.loaded / next.total;
          } else if (next.type == HttpEventType.Response) {
            this.log.log("Image upload response", next);
            this.upload.uploaded = 100;
            let body = next.body as string[];
            if (body.length > 0) {
              let imageId = body[0];
              let imageUrl = `${this.imageEndpoint}${imageId}`;
              this.setImageUrl(imageUrl);
              this.upload = null;
            }

          }
        },
            (error) => this.log.error(error)
      );
    }
  }

  setImageUrl(imageUrl: string) {
    this.imageUrl = imageUrl;
    this.imageUrlChange.emit(imageUrl);
  }

  private loadPreview(file, callback) {
    let reader = new FileReader();
    reader.addEventListener("load", function() {
      callback(reader.result);
    }, false);
    if (file) {
      reader.readAsDataURL(file);
    }
  }

  private ensureWidth(file, width: number, callback) {
    this.ensureImage(file, callback, (canvas, img) => {
      let imgWidth = img.width;
      let imgHeight = img.height;

      let height = width * imgHeight/imgWidth;

      canvas.width = width;
      canvas.height = height;

      return {
        srcX: 0,
        srcY: 0,
        srcW: imgWidth,
        srcH: imgHeight,
        dstX: 0,
        dstY: 0,
        dstW: width,
        dstH: height
      }
    });
  }

  private ensureSize(file, width: number, height: number, callback) {
    const targetRatio = width / height;

    this.ensureImage(file, callback, (canvas, img) => {

      // set its dimension to target size
      canvas.width = width;
      canvas.height = height;

      const imgWidth = img.width;
      const imgHeight = img.height;
      const imgRatio = imgWidth / imgHeight;

      let srcX = 0, srcY = 0, srcW = imgWidth, srcH = imgHeight;
      if (imgRatio != targetRatio) {
        let aspectHeight, aspectWidth;
        if (targetRatio == 1) {
          aspectHeight = aspectWidth = Math.min(imgWidth, imgHeight);
        } else {
          aspectHeight = imgWidth / targetRatio;
          aspectWidth = imgHeight * targetRatio;
        }

        if (aspectHeight < imgHeight) {
          srcX = 0;
          srcY = (imgHeight - aspectHeight) / 2.0;
          srcW = imgWidth;
          srcH = aspectHeight;
        } else if (aspectWidth < imgWidth) {
          srcX = (imgWidth - aspectWidth) / 2.0;
          srcY = 0;
          srcW = aspectWidth;
          srcH = imgHeight;
        }
      }
      return {
        srcX,
        srcY,
        srcW,
        srcH,
        dstX: 0,
        dstY: 0,
        dstW: width,
        dstH: height
      };
    });
  }

  private ensureImage(file, callback, imgProcessingCallback) {
    // create an off-screen canvas
    const canvas = document.createElement('canvas'),
      ctx = canvas.getContext('2d');

    let img = new Image;
    let url = URL.createObjectURL(file);
    img.addEventListener("load", () => {

      let result = imgProcessingCallback(canvas, img);

      ctx.drawImage(img, result.srcX, result.srcY, result.srcW, result.srcH, result.dstX, result.dstY, result.dstW, result.dstH);

      callback(canvas.toDataURL());
      URL.revokeObjectURL(url);
    });

    img.src = url;
  }

  cancelUpload() {
    this.uploadSubscription.unsubscribe();
  }

  public draggingFile: boolean = false;

  onDragEnter($event) {
    if (this.acceptDrop() && this.isFileDrag($event)) {
      this.draggingFile = true;
    }
  }

  private acceptDrop() {
    return !this.upload && !this.imageUrl;
  }

  private isFileDrag($event: DragEvent) {
    return $event.dataTransfer.types.indexOf("Files") != -1;
  }

  onDragLeave($event) {
    this.draggingFile = false;
  }

  onDrop($event: DragEvent) {
    this.draggingFile = false;
    if (this.acceptDrop() && this.isFileDrag($event)) {
      this.log.log("Drop event", $event);
      $event.preventDefault();
      $event.stopPropagation();

      if ($event.dataTransfer.files.length > 0) {
        this.startUploadingImage($event.dataTransfer.files.item(0));
      }
    }

    return false;
  }

  onDragOver($event) {
    if (this.acceptDrop()) {
      $event.preventDefault();
      return false;
    }
  }

  cardImageUrl() {
    if (this.imageUrl) {
      return this.imageUrl
    } else if (this.upload) {
      if (this.upload.url) {
        return this.upload.url;
      } else {
        return 'assets/loading.svg';
      }
    } else {
      return 'assets/placeholder.svg';
    }
  }
}


