import { Component, OnInit, Input } from '@angular/core';
import * as L from 'leaflet';
import { Calendar } from '../model/calendar';
import zoom = L.control.zoom;
import {control, latLng, LeafletMouseEvent, marker} from "leaflet";
import layers = control.layers;

@Component({
  selector: 'sc-calendar-map',
  templateUrl: './calendar-map.component.html',
  styleUrls: ['./calendar-map.component.scss']
})
export class CalendarMapComponent {

  @Input() calendar: Calendar;
  @Input() mapReadonly: boolean;

  mapLat = this.calendar && this.calendar.latitude ? this.calendar.latitude : -28;
  mapLng = this.calendar && this.calendar.longitude ? this.calendar.longitude : 134;

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

  constructor(
  ) { }

  onMapReady(map: L.Map) {
    console.log('onMapReady');
    map.invalidateSize(false);
   // map.scrollWheelZoom.disable();
    map.fitBounds(
         [[-43.6345972634, 113.338953078], [-10.6681857235, 153.569469029]], {minZoom: 4, maxZoom: 15}
    map.setMaxBounds([[-43.6345972634, 113.338953078], [-10.6681857235, 153.569469029]]);

    // Add listener when on Draw Create
    map.on( L.Draw.Event.CREATED, <LeafletDrawEvent> (e) => {
      map.eachLayer(layer =>  {
        if (layer instanceof L.Marker) {
          if (layer != e.layer) {
            console.log("Delete": + layer.getLatLng());
            map.removeLayer(layer);
          }
        }
      });
    });

    map.on(L.Draw.Event.DRAWSTOP, event2 => {
      map.eachLayer(layer =>  {
        if (layer instanceof L.Marker) {
          console.log(layer.getLatLng());
          this.calendar.latitude = layer.getLatLng().lat;
          this.calendar.longitude = layer.getLatLng().lng;
        }

      });
    });

  };


}
