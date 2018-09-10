import {Uuid} from "../shared/uuid";

export interface IFeature {
  profileUuid: string | null | undefined;
  name: string;
  commonName: string | null | undefined;
  scientificName: string | null | undefined;
  description: string;
  imageUrls: string[];
}

export class Feature implements IFeature {

  private tempKey: string = null;

  constructor(
    public profileUuid: string | null | undefined = '',
    public name: string = '',
    public commonName: string | null | undefined = '',
    public scientificName: string | null | undefined = '',
    public description: string = '',
    public imageUrls: string[] = []
  ) { }

  public static fromJson(feature: IFeature) {
    return new Feature(
      feature.profileUuid,
      feature.name,
      feature.commonName,
      feature.scientificName,
      feature.description,
      feature.imageUrls.map((value) => value)
    );
  }

  clone() {
    return Feature.fromJson(this);
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
