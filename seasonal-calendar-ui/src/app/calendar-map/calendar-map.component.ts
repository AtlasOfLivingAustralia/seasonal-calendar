import {Component, Input, Output, EventEmitter, NgZone} from '@angular/core';
import * as L from 'leaflet';
import {LeafletEvent} from "leaflet";


@Component({
  selector: 'sc-calendar-map',
  templateUrl: './calendar-map.component.html',
  styleUrls: ['./calendar-map.component.scss']
})
export class CalendarMapComponent {

  @Input() latitude: number;
  @Output() latitudeChange = new EventEmitter<number>();
  @Input() longitude: number;
  @Output() longitudeChange = new EventEmitter<number>();
  @Input() zoom: number;
  @Output() zoomChange = new EventEmitter<number>();

  @Input() mapReadonly: boolean;

  mapLat = this.latitude ? this.latitude : -28;
  mapLng = this.longitude ? this.longitude : 134;

  //baseMap = 'https://toolserver.org/tiles/hikebike/${z}/${x}/${y}.png';
 // baseMap = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
  baseMap = 'https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png';
  //baseMap = 'http://{s}.tile.osm.org/{z}/{x}/{y}.png';

  options = {
    layers: [L.tileLayer(this.baseMap, { maxZoom: 18, attribution: 'Open Street Map' })
    ],
    center: [this.mapLat, this.mapLng],
    zoom: 4,
    maxZoom: 20,
    minZoom: 4,
    maxAutoZoom: 5,
  };

  markerOptions = L.icon({
    iconSize: [ 25, 41 ],
    iconAnchor: [ 13, 41 ],
    iconUrl: 'leaflet/marker-icon.png',
    shadowUrl: 'leaflet/marker-shadow.png'
  });

  marker = L.marker([ this.mapLat, this.mapLng], {
    icon: this.markerOptions
  });

 // markers = new L.FeatureGroup();
 // markers.addLayer(this.marker);

  layers = [this.marker];

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

  private map: L.Map;

  constructor(
    private zone: NgZone
  ) { }

  onMapReady(map: L.Map) {
    this.map = map;
    console.log('onMapReady');
    map.invalidateSize(false);
   // map.scrollWheelZoom.disable();

    let bounds: L.LatLngBoundsExpression = [[-43.6345972634, 113.338953078], [-10.6681857235, 153.569469029]];
    let options = {minZoom: 4, maxZoom: 15};
    map.fitBounds(bounds, options);
    map.setMaxBounds(bounds);

    // Add listener when on Draw Create
    map.on( L.Draw.Event.CREATED, <LeafletDrawEvent> (e) => {
      map.eachLayer(layer =>  {
        if (layer instanceof L.Marker) {
          if (layer != e.layer) {
            console.log("Delete:" + layer.getLatLng());
            map.removeLayer(layer);
          }
        }
      });
    });

    map.on(L.Draw.Event.DRAWSTOP, event2 => {
      map.eachLayer(layer =>  {
        if (layer instanceof L.Marker) {
          console.log(layer.getLatLng());
          this.updateLatitude(layer.getLatLng().lat);
          this.updateLongitude(layer.getLatLng().lng);
        }

      });
    });

  };

  mapZoomChange($event: LeafletEvent) {
    console.log("mapZoomChange");
    let zoom = this.map.getZoom();
    console.log(`zoom is ${zoom}`);
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
