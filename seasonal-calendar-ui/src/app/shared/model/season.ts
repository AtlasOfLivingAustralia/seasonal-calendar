import {IFeature, Feature} from "./feature";
import {Uuid} from "../uuid";

export interface ISeason {
  id: number | null | undefined
  localName: string
  alternateName: string | null | undefined
  startMonth: number
  endMonth: number
  weatherIcons: string | null | undefined
  description: string
  features: IFeature[];
}

export class Season implements ISeason {

  private tempKey: string = null;

  public id: number | null | undefined = null;
  public localName: string = '';
  public alternateName: string | null | undefined = '';
  public startMonth: number = 1;
  public endMonth: number = 12;
  public weatherIcons: string | null | undefined = '';
  public description: string = '';
  public features: IFeature[] = [];

  constructor(other?: ISeason) {
    if (other) {
      this.id = other.id;
      this.localName = other.localName;
      this.alternateName = other.alternateName;
      this.startMonth = other.startMonth;
      this.endMonth = other.endMonth;
      this.weatherIcons = other.weatherIcons;
      this.description = other.description;
      this.features = other.features.map((value) => Feature.fromJson(value));
    }
  }

  static fromJson(season: ISeason) {
    return new Season(season);
  }

  clone() {
    return new Season(this);
  }

  getKey() {
    if (this.id != null) {
      return this.id;
    } else {
      if (this.tempKey == null) {
        this.tempKey = Uuid.newUuid();
      }
      return this.tempKey;
    }
  }
}
