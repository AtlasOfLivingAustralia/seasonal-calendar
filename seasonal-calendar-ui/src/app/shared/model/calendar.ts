import {ISeason, Season} from "./season";
import {Uuid} from "../uuid";

export interface ICalendar {
  collectionUuid: string;
  shortName: string;
  name: string;
  description: string;
  imageUrl: string;
  websiteUrl: string;
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
  welcomeCountry: string;
  welcomeCountryMedia: string;
  whoWeAreDescription: string;
  ourCountry: string;
  ourHistory: string;
  logos: string[];
  mediaLinks: string[];
  externalLinks: string[];
  admin?: boolean;
  users?: {username: string, admin: boolean, editor: boolean}[];
}

export class Calendar implements ICalendar {

  private tempKey: string = null;

  public collectionUuid: string = '';
  public shortName: string = '';
  public name: string = '';
  public description: string = '';
  public imageUrl: string = '';
  public websiteUrl: string = '';
  public organisationName: string = '';
  public contributors: string[] = [];
  public contactName: string = '';
  public contactEmail: string = '';
  public keywords: string[] = [];
  public about: string = '';
  public organisationUrl: string = '';
  public organisationLogoUrl: string = '';
  public development: string = '';
  public references: string[] = [];
  public referenceLinks: string[] = [];
  public developmentReason: string = '';
  public limitations: string = '';
  public licenceTerms: string = '';
  public latitude: number = 0;
  public longitude: number = 0;
  public zoom: number = 0;
  public languageGroup: string = '';
  public published: boolean = false;
  public seasons: ISeason[] = [];
  public welcomeCountry: string = '';
  public welcomeCountryMedia: string = '';
  public whoWeAreDescription: string = '';
  public ourCountry: string = '';
  public ourHistory: string = '';
  public logos: string[] = [];
  public mediaLinks: string[] = [];
  public externalLinks: string[] = [];
  public admin?: boolean = false;
  public users?: {username: string, admin: boolean, editor: boolean}[] = [];

  constructor(other?: ICalendar) {
    if (other) {
      this.collectionUuid = other.collectionUuid;
      this.shortName = other.shortName;
      this.name = other.name;
      this.description = other.description;
      this.imageUrl = other.imageUrl;
      this.websiteUrl = other.websiteUrl;
      this.organisationName = other.organisationName;
      this.contributors = other.contributors.slice(0);
      this.contactName = other.contactName;
      this.contactEmail = other.contactEmail;
      this.keywords = other.keywords.slice(0);
      this.about = other.about;
      this.organisationUrl = other.organisationUrl;
      this.organisationLogoUrl = other.organisationLogoUrl;
      this.development = other.development;
      this.references = other.references.slice(0);
      this.referenceLinks = other.referenceLinks.slice(0);
      this.developmentReason = other.developmentReason;
      this.limitations = other.limitations;
      this.licenceTerms = other.licenceTerms;
      this.latitude = other.latitude;
      this.longitude = other.longitude;
      this.zoom = other.zoom;
      this.languageGroup = other.languageGroup;
      this.published = other.published;
      this.seasons = other.seasons.map((value) => Season.fromJson(value));
      this.welcomeCountry = other.welcomeCountry;
      this.welcomeCountryMedia = other.welcomeCountryMedia;
      this.whoWeAreDescription = other.whoWeAreDescription;
      this.ourCountry = other.ourCountry;
      this.ourHistory = other.ourHistory;
      this.logos = other.logos.slice(0);
      this.mediaLinks = other.mediaLinks.slice(0);
      this.externalLinks = other.externalLinks.slice(0);
      this.admin = other.admin;
      this.users = other.users ? other.users.slice(0) : other.users;
    }
  }

  public static fromJson(calendar: ICalendar): Calendar {
    return new Calendar(calendar);
  }

  clone(): Calendar {
    return new Calendar(this);
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

export interface CalendarSaved {
  collectionUuid: string;
}
