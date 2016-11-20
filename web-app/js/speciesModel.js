/**
 * Manages the species data type in the output model.
 * Allows species information to be searched for and displayed.
 */
var SpeciesViewModel = function (species, lists, populate) {
    var self = this;
    if (!species) species = {};
    if (!lists) lists = {};
    if (!populate) populate = false;
    self.name = ko.observable(species.name);
    self.guid = ko.observable(species.guid);
    self.scientificName = ko.observable(species.scientificName);
    self.commonName = ko.observable(species.commonName);

    // Reference to output species uuid - for uniqueness retrieved from ecodata server
    self.outputSpeciesId = ko.observable();

    self.transients = {};
    self.transients.name = ko.observable(species.name);
    self.transients.guid = ko.observable(species.guid);
    self.transients.scientificName = ko.observable(species.scientificName);
    self.transients.commonName = ko.observable(species.commonName);
    self.transients.source = ko.observable(scConfig.speciesSearch);
    self.transients.bieUrl = ko.observable();

    self.transients.bioProfileUrl = ko.computed(function () {
        return self.transients.bieUrl();
    });

    self.focusLost = function (event) {
        self.name(self.transients.name());
        self.guid(self.transients.guid());
        self.scientificName(self.transients.scientificName());
        self.commonName(self.transients.commonName());
        self.transients.bieUrl(scConfig.bieUrl + '/species/' + self.guid());
    };

    self.transients.guid.subscribe(function (newValue) {
        self.name(self.transients.name());
        self.guid(self.transients.guid());
        self.scientificName(self.transients.scientificName());
        self.commonName(self.transients.commonName());
        self.transients.bieUrl(scConfig.bieUrl + '/species/' + self.guid());
    });

    self.populateSingleSpecies = function (populate) {
        if (!self.name() && !self.guid() && scConfig.getSingleSpeciesUrl && populate) {
            $.ajax({
                url: scConfig.getSingleSpeciesUrl,
                type: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    if (data.name && data.guid) {
                        self.name(data.name);
                        self.guid(data.guid);
                        self.scientificName(data.scientificName);
                        self.commonName(data.commonName);
                        self.transients.name(data.name);
                        self.transients.guid(data.guid);
                        self.transients.scientificName(data.scientificName);
                        self.transients.commonName(data.commonName);
                    }
                },
                error: function (data) {
                    console.log('Error retrieving single species')
                }
            });
        }
    };

    self.reset = function () {
        self.name("");
        self.guid("");
        self.scientificName("");
        self.commonName("");
        self.transients.name("");
        self.transients.guid("");
        self.transients.scientificName("");
        self.transients.commonName("");
    };

    self.loadSpecies = function (species) {
        if(!species) species = {};

        self.name(species.name);
        self.guid(species.guid);
        self.scientificName(species.scientificName);
        self.commonName(species.commonName);
        self.transients.name(species.name);
        self.transients.guid(species.guid);
        self.transients.scientificName(species.scientificName);
        self.transients.commonName(species.commonName);
    };

    self.populateSingleSpecies(populate);
};
