import {Uuid} from "../shared/uuid";

export class Feature {

  private tempKey: string = null;

  constructor(
    public profileUuid: string | null | undefined = '',
    public name: string = '',
    public commonName: string | null | undefined = '',
    public scientificName: string | null | undefined = '',
    public description: string = '',
    public imageUrls: string[] = []
  ) { }

  clone() {
    return new Feature(
      this.profileUuid,
      this.name,
      this.commonName,
      this.scientificName,
      this.description,
      this.imageUrls.map((value) => value)
    );
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
