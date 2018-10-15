import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpEventType} from "@angular/common/http";
import {Subscription} from "rxjs";
import {Upload} from "../../shared/model/image-upload";
import {environment} from "../../../environments/environment";
import {Logger} from "../../shared/services/logger.service";
import {CalendarService} from "../../shared/services/calendar.service";

interface ImageUpload {
  upload: Upload;
  imageUrl: string;
  subscription: Subscription;
}

@Component({
  selector: 'sc-image-upload-modal',
  templateUrl: './image-upload-modal.component.html',
  styleUrls: ['./image-upload-modal.component.scss']
})
export class ImageUploadModalComponent {

  @Input() title: string = "Upload image";
  @Input() label: string = "Select file";
  @Input() class: string = "btn btn-primary";
  @Input() multiple: boolean = true;
  @Input() accept: string = 'image/*';
  _imageUrls: string[] = [];
  images: ImageUpload[] = [];
  private readonly imageEndpoint: string = `${environment.api}images/`;

  @Input() set imageUrls(imageUrls: string[]) {
    this._imageUrls = imageUrls;
    this.images = imageUrls.map((val, idx, arr) => ({ imageUrl: val, upload: null, subscription: null}));
  };

  @ViewChild('file') file: ElementRef;

  constructor(
    public activeModal: NgbActiveModal,
    private log: Logger,
    private calendarService: CalendarService
  ) { }

  selectFile() {
    let ne = this.file.nativeElement;
    if (ne != null) {
      ne.click();
    }
  }

  imageChanged($event) {
    let files: FileList = $event.target.files;
    for (let i = 0; i < files.length; ++i) {
      let file = files.item(i);
      this.startUploadingImage(file);
      // file.
    }
    this.log.log($event);
  }

  private startUploadingImage(file: File) {
    if (file) {
      let uploading: Upload = {
        filename: file.name,
        size: file.size,
        url: null,
        uploaded: 0
      };
      let imageUpload = { imageUrl: null, upload: uploading, subscription: null};
      this.images.push(imageUpload);
      this.ensureSize(file, 64, 64, (url) => uploading.url = url);

      imageUpload.subscription = this.calendarService.uploadImages([file]).subscribe(
        (next) => {
          if (next.type == HttpEventType.UploadProgress) {
            this.log.log("Image upload progress", next);
            imageUpload.upload.uploaded = next.loaded / next.total;
          } else if (next.type == HttpEventType.Response) {
            this.log.log("Image upload response", next);
            imageUpload.upload.uploaded = 100;
            let body = next.body as string[];
            if (body.length > 0) {
              let imageId = body[0];
              let imageUrl = `${this.imageEndpoint}${imageId}`;
              imageUpload.imageUrl = imageUrl;
              imageUpload.upload = null;
              imageUpload.subscription = null;
            }

          }
        },
        (error) => this.log.error(error)
      );
    }
  }

  private ensureSize(file, width: number, height: number, callback) {
    // create an off-screen canvas
    const canvas = document.createElement('canvas'),
      ctx = canvas.getContext('2d');

    const targetRatio = width / height;

    let img = new Image;
    let url = URL.createObjectURL(file);
    img.addEventListener("load", () => {

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

      ctx.drawImage(img, srcX, srcY, srcW, srcH, 0, 0, width, height);

      callback(canvas.toDataURL());
      URL.revokeObjectURL(url);
    });

    img.src = url;
    // set its dimension to target size
    canvas.width = width;
    canvas.height = height;

  }

  cardImageUrl(imageUpload: ImageUpload) {
    if (imageUpload.imageUrl) {
      return imageUpload.imageUrl;
    } else if (imageUpload.upload) {
      if (imageUpload.upload.url) {
        return imageUpload.upload.url;
      } else {
        return 'assets/loading.svg';
      }
    } else {
      return 'assets/placeholder.svg';
    }
  }

  cancelUpload(index: number) {
    let upload = this.images[index];
    if (upload.subscription) {
      upload.subscription.unsubscribe();
    }
    this.images.slice(index);
  }

  isUploading() {
    this.images.some((val, idx, arr) => val.upload != null);
  }

  cancel() {
    this.images.forEach( (val, idx, arr) => {
      if (val.subscription != null) {
        val.subscription.unsubscribe();
      }
    });
    this.activeModal.dismiss("cancel");
  }

  save() {
    this.activeModal.close(this.images.map((val, idx, arr) => val.imageUrl));
  }

  removeImage(index: number) {
    let image = this.images[index];
    if (image.subscription) {
      image.subscription.unsubscribe();
    }
    this.images.splice(index, 1);
  }
}
