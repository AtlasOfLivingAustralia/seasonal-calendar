import {Component, Input, OnChanges, SimpleChanges} from "@angular/core";
import * as L from "leaflet";

@Component({
  selector: 'sc-calendar-map',
  templateUrl: './calendar-map.component.html',
  styleUrls: ['./calendar-map.component.scss']
})
export class CalendarMapComponent implements OnChanges {
  @Input() latitude: number;
  @Input() longitude: number;
  @Input() zoom: number;

  readonly DEFAULT_CENTRE_LAT = -28;
  readonly DEFAULT_CENTRE_LNG = 134;
  readonly DEFAULT_ZOOM = 4;

  //baseMap = 'https://toolserver.org/tiles/hikebike/${z}/${x}/${y}.png';
  // baseMap = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
  baseMap = 'https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png';
  //baseMap = 'http://{s}.tile.osm.org/{z}/{x}/{y}.png';

  options = null;

  markerOptions = L.icon({
    iconSize: [ 25, 41 ],
    iconAnchor: [ 13, 41 ],
    iconUrl: 'leaflet/marker-icon.png',
    shadowUrl: 'leaflet/marker-shadow.png'
  });

  marker: L.Marker;
  // L.marker([ this.latitude, this.longitude], {
  //   icon: this.markerOptions
  // });

  layers: Array<L.Marker>;

  protected map: L.Map;

  constructor(
  ) { }

  onMapReady(map: L.Map) {
    this.map = map;
    console.log('onMapReady');
    map.invalidateSize(false);
    // map.scrollWheelZoom.disable();

    let bounds: L.LatLngBoundsExpression = [[-43.6345972634, 113.338953078], [-10.6681857235, 153.569469029]];
    // let options = {minZoom: 4, maxZoom: 15};
    // map.fitBounds(bounds, options);
    map.setMaxBounds(bounds);

    this.moveMarker();

  };

  ngOnChanges(changes: SimpleChanges) {
    if (this.map) {
      this.moveMarker();
    } else if (!this.options) {
      // wait till input variables are set before initialising the map options so we start
      // on the correct point / zoom
      let centerLat = this.latitude ? this.latitude : this.DEFAULT_CENTRE_LAT;
      let centerLng = this.longitude ? this.longitude : this.DEFAULT_CENTRE_LNG;
      let zoom = this.zoom ? this.zoom : this.DEFAULT_ZOOM;
      this.options = {
        layers: [
          L.tileLayer(this.baseMap, {maxZoom: 18, attribution: 'Open Street Map'})
        ],
        center: [centerLat, centerLng],
        zoom: zoom,
        maxZoom: 20,
        minZoom: 4,
        maxAutoZoom: 5,
      };

      this.marker = L.marker([ centerLat, centerLng], {
        icon: this.markerOptions
      });
      this.layers = [this.marker];
    }
  }

  moveMarker() {
    if (this.marker.getLatLng().lat != this.latitude || this.marker.getLatLng().lng != this.longitude) {
      this.marker.setLatLng({lat: this.latitude, lng: this.longitude});
    }
    if (this.map.getCenter().lat != this.latitude || this.map.getCenter().lng != this.longitude || this.map.getZoom() != this.zoom) {
      this.map.setView({lat: this.latitude, lng: this.longitude}, this.zoom);
    }
  }
}
