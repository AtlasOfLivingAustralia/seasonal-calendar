import {Component, Input, Output, EventEmitter, NgZone} from '@angular/core';
import * as L from 'leaflet';
import {LeafletEvent} from "leaflet";
import {CalendarMapComponent} from "./calendar-map.component";


@Component({
  selector: 'sc-calendar-draw-map',
  templateUrl: './calendar-draw-map.component.html',
  styleUrls: ['./calendar-map.component.scss']
})
export class CalendarDrawMapComponent extends CalendarMapComponent {

  @Output() latitudeChange = new EventEmitter<number>();
  @Output() longitudeChange = new EventEmitter<number>();
  @Output() zoomChange = new EventEmitter<number>();

  @Input() mapReadonly: boolean = false;


  drawOptions = {
    draw: {
      polyline: false,
      polygon: false,
      rectangle: false,
      circle: false,
      circlemarker: false,
      marker: {
        icon: L.icon({
          iconSize: [ 25, 41 ],
          iconAnchor: [ 13, 41 ],
          iconUrl: 'leaflet/marker-icon.png',
          shadowUrl:  'leaflet/marker-shadow.png'
        })
      }
    },
    edit: {
     // featureGroup: new L.featureGroup(),
      remove: false,
      edit: false
    },
  };

  constructor(
    private zone: NgZone
  ) { super() }

  onMapReady(map: L.Map) {
    super.onMapReady(map);

    if (!this.mapReadonly) {
      // Add listener when on Draw Create
      this.map.on( L.Draw.Event.CREATED, <LeafletDrawEvent> (e) => {
        this.map.eachLayer(layer =>  {
          if (layer instanceof L.Marker) {
            if (layer != e.layer) {
              console.log("Delete:" + layer.getLatLng());
              this.map.removeLayer(layer);
            }
          }
        });
      });

      this.map.on(L.Draw.Event.DRAWSTOP, event2 => {
        this.map.eachLayer(layer =>  {
          console.log( typeof layer);
          if (layer instanceof L.Marker) {
            console.log(layer.getLatLng());
            this.updateLatitude(layer.getLatLng().lat);
            this.updateLongitude(layer.getLatLng().lng);
          }

        });
      });
    }

  };

  mapZoomChange($event: LeafletEvent) {
    let zoom = this.map.getZoom();
    // don't need to run this in an angular zone because
    // the ngx-leaflet emitter it's bound to already does it.
    this.zoom = zoom;
    this.zoomChange.emit(zoom);
  }

  private updateLatitude(latitude: number) {
    this.latitude = latitude;
    this.handleUpdate(latitude, this.latitudeChange);
  }

  private updateLongitude(longitude: number) {
    this.longitude = longitude;
    this.handleUpdate(longitude, this.longitudeChange);
  }

  private handleUpdate<T>(value: T, change: EventEmitter<T>) {
    if (change.observers.length > 0) {
      this.zone.run(() => change.emit(value));
    }
  }
}
