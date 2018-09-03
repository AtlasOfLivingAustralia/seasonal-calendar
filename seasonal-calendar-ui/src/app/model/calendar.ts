import {Season} from "./season";

export class Calendar {
  constructor(
    public collectionUuid: string,
    public name: string,
    public description: string,
    public imageUrl: string,
    public websiteUrl: string,
    public youtubeId: string,
    public organisationName: string,
    public contributors: string[] = [],
    public about: string,
    public organisationUrl: string,
    public organisationLogoUrl: string,
    public development: string,
    public references: string[] = [],
    public referenceLinks: string[] = [],
    public developmentReason: string,
    public limitations: string,
    public licenceTerms: string,
    public latitiude: number,
    public longitude: number,
    public zoom: number,
    public languageGroup: string,
    public published: boolean,
    public seasons: Season[] = []
  ) { }
}
