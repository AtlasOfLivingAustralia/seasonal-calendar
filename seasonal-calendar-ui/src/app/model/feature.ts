
export class Feature {
  constructor(
    public profileUuid: string | null | undefined,
    public name: string,
    public commonName: string | null | undefined,
    public scientificName: string | null | undefined,
    public description: string,
    public imageUrls: string[] = []
  ) { }
}
