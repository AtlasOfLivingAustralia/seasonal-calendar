import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {IFeature} from "../model/feature";
import {NgbModal, NgbModalOptions} from '@ng-bootstrap/ng-bootstrap';
import {ImageUploadModalComponent} from "../image-upload-modal/image-upload-modal.component";
import {Observable, of} from "rxjs";
import {catchError, debounceTime, distinctUntilChanged, map, switchMap, tap} from "rxjs/operators";
import {Logger} from "../shared/logger.service";
import {CalendarService} from "../calendar.service";
import {BieSearchResult} from "../model/bie-search-response";

@Component({
  selector: 'sc-feature-edit',
  templateUrl: './feature-edit.component.html',
  styleUrls: ['./feature-edit.component.scss']
})
export class FeatureEditComponent implements OnInit {

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

  ngOnInit() {
  }

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
