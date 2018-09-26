import {Uuid} from "../shared/uuid";

export interface IFeature {
  profileUuid: string | null | undefined;
  name: string;
  commonName: string | null | undefined;
  scientificName: string | null | undefined;
  scientificNameGuid: string | null | undefined;
  description: string;
  imageUrls: string[];
}

export class Feature implements IFeature {

  private tempKey: string = null;

  public profileUuid: string | null | undefined = '';
  public name: string = '';
  public commonName: string | null | undefined = '';
  public scientificName: string | null | undefined = '';
  public scientificNameGuid: string | null | undefined = '';
  public description: string = '';
  public imageUrls: string[] = [];

  constructor(other?: IFeature) {
    if (other) {
      this.profileUuid = other.profileUuid;
      this.name = other.name;
      this.commonName = other.commonName;
      this.scientificName = other.scientificName;
      this.scientificNameGuid = other.scientificNameGuid;
      this.description = other.description;
      this.imageUrls = other.imageUrls.slice(0);
    }
  }

  public static fromJson(feature: IFeature) {
    return new Feature(feature);
  }

  clone() {
    return new Feature(this);
  }

  getKey() {
    if (this.profileUuid != '') {
      return this.profileUuid;
    } else {
      if (this.tempKey == null) {
        this.tempKey = Uuid.newUuid();
      }
      return this.tempKey;
    }
  }
}
