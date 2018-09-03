import {Feature} from "./feature";

export class Season {
  constructor(
    public id: number | null | undefined,
    public localName: string,
    public alternateName: string | null | undefined,
    public startMonth: number,
    public endMonth: number,
    public weatherIcons: string | null | undefined,
    public description: string,
    public features: Feature[] = []
  ) { }
}
