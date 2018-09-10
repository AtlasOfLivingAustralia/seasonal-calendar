import {IFeature, Feature} from "./feature";
import {Uuid} from "../shared/uuid";

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

  constructor(
    public id: number | null | undefined = null,
    public localName: string = '',
    public alternateName: string | null | undefined = '',
    public startMonth: number = 1,
    public endMonth: number = 12,
    public weatherIcons: string | null | undefined = '',
    public description: string = '',
    public features: IFeature[] = []
  ) { }

  static fromJson(season: ISeason) {
    return new Season(
      season.id,
      season.localName,
      season.alternateName,
      season.startMonth,
      season.endMonth,
      season.weatherIcons,
      season.description,
      season.features.map((value => Feature.fromJson(value)))
    );
  }

  clone() {
    return Season.fromJson(this);
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
