import {Season} from "./season";
import {Uuid} from "../shared/uuid";

export class Calendar {

  private tempKey: string = null;

  constructor(
    public collectionUuid: string = '',
    public shortName: string = '',
    public name: string = '',
    public description: string = '',
    public imageUrl: string = '',
    public websiteUrl: string = '',
    public youtubeId: string = '',
    public organisationName: string = '',
    public contributors: string[] = [],
    public contactName: string = '',
    public contactEmail: string = '',
    public keywords: string[] = [],
    public about: string = '',
    public organisationUrl: string = '',
    public organisationLogoUrl: string = '',
    public development: string = '',
    public references: string[] = [],
    public referenceLinks: string[] = [],
    public developmentReason: string = '',
    public limitations: string = '',
    public licenceTerms: string = '',
    public latitiude: number = 0,
    public longitude: number = 0,
    public zoom: number = 0,
    public languageGroup: string = '',
    public published: boolean = false,
    public seasons: Season[] = []
  ) { }


  clone(): Calendar {
    return new Calendar(
      this.collectionUuid,
      this.shortName,
      this.name,
      this.description,
      this.imageUrl,
      this.websiteUrl,
      this.youtubeId,
      this.organisationName,
      this.contributors.map((value) => value),
      this.contactName,
      this.contactEmail,
      this.keywords.map((value) => value),
      this.about,
      this.organisationUrl,
      this.organisationLogoUrl,
      this.development,
      this.references.map((value) => value),
      this.referenceLinks.map((value) => value),
      this.developmentReason,
      this.limitations,
      this.licenceTerms,
      this.latitiude,
      this.longitude,
      this.zoom,
      this.languageGroup,
      this.published,
      this.seasons.map((value) => value.clone())
    )
  }

  getKey() {
    if (this.collectionUuid != '') {
      return this.collectionUuid;
    } else {
      if (this.tempKey == null) {
        this.tempKey = Uuid.newUuid();
      }
      return this.tempKey;
    }
  }
}
