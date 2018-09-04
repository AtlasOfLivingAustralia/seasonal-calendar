
export class Feature {
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
}
