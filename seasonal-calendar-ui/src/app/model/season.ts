import {Feature} from "./feature";

export class Season {
  constructor(
    public id: number | null | undefined = null,
    public localName: string = '',
    public alternateName: string | null | undefined = '',
    public startMonth: number = 1,
    public endMonth: number = 12,
    public weatherIcons: string | null | undefined = '',
    public description: string = '',
    public features: Feature[] = []
  ) { }

  clone() {
    return new Season(
      this.id,
      this.localName,
      this.alternateName,
      this.startMonth,
      this.endMonth,
      this.weatherIcons,
      this.description,
      this.features.map((value => value.clone()))
    );

  }
}
