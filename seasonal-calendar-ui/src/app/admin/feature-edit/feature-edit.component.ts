import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgbModal, NgbModalOptions} from '@ng-bootstrap/ng-bootstrap';
import {ImageUploadModalComponent} from "../image-upload-modal/image-upload-modal.component";
import {Observable, of} from "rxjs";
import {catchError, debounceTime, distinctUntilChanged, map, switchMap, tap} from "rxjs/operators";
import {IFeature} from "../../shared/model/feature";
import {BieSearchResult} from "../../shared/model/bie-search-response";
import {CalendarService} from "../../shared/services/calendar.service";
import {Logger} from "../../shared/services/logger.service";

@Component({
  selector: 'sc-feature-edit',
  templateUrl: './feature-edit.component.html'
})
export class FeatureEditComponent {

  @Input() feature : IFeature;
  @Output() delete = new EventEmitter();
  // @Output() featureChange = new EventEmitter<Feature>();

  searching = false;
  searchFailed = false;

  searchTerm: string;

  private _selectedResponse: BieSearchResult;
  get selectedResponse(): BieSearchResult {
    return this._selectedResponse;
  };
  set selectedResponse(selectedResponse: BieSearchResult) {
    this._selectedResponse = selectedResponse;
    this.feature.scientificName = selectedResponse.name;
    this.feature.scientificNameGuid = selectedResponse.guid;
    if (!this.feature.commonName && selectedResponse.commonName) this.feature.commonName = selectedResponse.commonName;
  }

  constructor(private calendarService: CalendarService,
              private log: Logger,
              private modalService: NgbModal
  ) { }

  trackByIndex(index, item) {
    return index;
  }

  deleteFeature() {
    this.delete.emit();
  }

  imageUploadModal(feature: IFeature) {
    const modalOptions: NgbModalOptions = {
      size: 'lg'
    };
    const modalRef = this.modalService.open(ImageUploadModalComponent, modalOptions);
    modalRef.componentInstance.title = `Upload images for ${feature.name}`;
    modalRef.componentInstance.label = "Select feature images";
    modalRef.componentInstance.imageUrls = feature.imageUrls;

    modalRef.result.then((result) => {
      if (result instanceof Array) {
        feature.imageUrls = result;
      } else {
        this.log.log("Got non array close result", result);
      }
    }, (reason) => {
    });
  }

  searchInputFormatter = (result: BieSearchResult) => {
    if (result.commonName) {
      return `${result.name} (${result.commonName})`;
    } else {
      return result.name;
    }
  };

  search = (text$: Observable<string>) => {
    return text$.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
      tap(() => this.searching = true),
      switchMap(term =>
        this.calendarService.speciesSearch(term).pipe(
          tap(() => this.searchFailed = false),
          map( (value, index) => value.autoCompleteList
            .filter((val, idx, arr) => val.name != null && val.name != '')
            // .map((val, idx, arr) => val.name)
          ),
          catchError(() => {
            this.searchFailed = true;
            return of([]);
          }))
      ),
      tap(() => this.searching = false)
    )
  }

}
