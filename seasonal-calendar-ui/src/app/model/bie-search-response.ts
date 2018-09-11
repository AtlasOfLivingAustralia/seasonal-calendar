export interface BieSearchResult {
  commonNameMatches: string[];
  name: string;
  occurenceCount: number;
  guid: string;
  scientificNameMatches: string[];
  rankString: string;
  matchedNames: string[];
  rankId: number;
  commonName?: string;
  georeferencedCount: number
}

export interface BieSearchResponse {
  autoCompleteList: BieSearchResult[]
}
