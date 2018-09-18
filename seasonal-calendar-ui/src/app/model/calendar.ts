import {ISeason, Season} from "./season";
import {Uuid} from "../shared/uuid";

export interface ICalendar {
  collectionUuid: string;
  shortName: string;
  name: string;
  description: string;
  imageUrl: string;
  websiteUrl: string;
  youtubeId: string;
  organisationName: string;
  contributors: string[];
  contactName: string;
  contactEmail: string;
  keywords: string[];
  about: string;
  organisationUrl: string;
  organisationLogoUrl: string;
  development: string;
  references: string[];
  referenceLinks: string[];
  developmentReason: string;
  limitations: string;
  licenceTerms: string;
  latitude: number;
  longitude: number;
  zoom: number;
  languageGroup: string;
  published: boolean;
  seasons: ISeason[];
}

export class Calendar implements ICalendar {

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
    public latitude: number = 0,
    public longitude: number = 0,
    public zoom: number = 0,
    public languageGroup: string = '',
    public published: boolean = false,
    public seasons: ISeason[] = []
  ) { }

  public static fromJson(calendar: ICalendar): Calendar {
    return new Calendar(
      calendar.collectionUuid,
      calendar.shortName,
      calendar.name,
      calendar.description,
      calendar.imageUrl,
      calendar.websiteUrl,
      calendar.youtubeId,
      calendar.organisationName,
      calendar.contributors.map((value) => value),
      calendar.contactName,
      calendar.contactEmail,
      calendar.keywords.map((value) => value),
      calendar.about,
      calendar.organisationUrl,
      calendar.organisationLogoUrl,
      calendar.development,
      calendar.references.map((value) => value),
      calendar.referenceLinks.map((value) => value),
      calendar.developmentReason,
      calendar.limitations,
      calendar.licenceTerms,
      calendar.latitude,
      calendar.longitude,
      calendar.zoom,
      calendar.languageGroup,
      calendar.published,
      calendar.seasons.map((value) => Season.fromJson(value))
    )
  }

  clone(): Calendar {
    return Calendar.fromJson(this);
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
