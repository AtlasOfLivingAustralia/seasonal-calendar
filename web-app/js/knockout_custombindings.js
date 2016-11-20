/**
 * Attaches a bootstrap popover to the bound element.  The details for the popover should be supplied as the
 * value of this binding.
 * e.g.  <a href="#" data-bind="popover: {title:"Popover title", content:"Popover content"}>My link with popover</a>
 *
 * The content and title must be supplied, other popover options have defaults.
 *
 */
ko.bindingHandlers.popover = {

    init: function (element, valueAccessor) {
        ko.bindingHandlers.popover.initPopover(element, valueAccessor);
    },
    update: function (element, valueAccessor) {

        var $element = $(element);
        $element.popover('destroy');
        var options = ko.bindingHandlers.popover.initPopover(element, valueAccessor);
        if (options.autoShow) {
            if ($element.data('firstPopover') === false) {
                $element.popover('show');
                $('body').on('click', function (e) {

                    if (e.target != element && $element.find(e.target).length == 0) {
                        $element.popover('hide');
                    }
                });
            }
            $element.data('firstPopover', false);
        }

    },

    defaultOptions: {
        placement: "right",
        animation: true,
        html: true,
        trigger: "hover"
    },

    initPopover: function (element, valueAccessor) {
        var options = ko.utils.unwrapObservable(valueAccessor());

        var combinedOptions = ko.utils.extend({}, ko.bindingHandlers.popover.defaultOptions);
        var content = ko.utils.unwrapObservable(options.content);
        ko.utils.extend(combinedOptions, options);
        combinedOptions.description = content;

        $(element).popover(combinedOptions);

        ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
            $(element).popover("destroy");
        });
        return options;
    }
};

ko.bindingHandlers.bootstrapPopover = {
    init: function(element, valueAccessor, allBindingsAccessor, viewModel) {
        var options = valueAccessor();
        var defaultOptions = {};
        options = $.extend(true, {}, defaultOptions, options);
        $(element).popover(options);
    }
};


/**
 * Handles tab selection / redirect.
 * If url is empty & tabId is set then initiates tab selection
 * If url is set then initiates redirect
 *
 * Example: data-bind="showTabOrRedirect: { url: '', tabId: '#activities-tab'}"
 */
ko.bindingHandlers.showTabOrRedirect = {
    'init': function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        var newValueAccesssor = function () {
            return function () {
                var options = ko.utils.unwrapObservable(valueAccessor());
                if (options.url == '' && options.tabId) {
                    $(options.tabId).tab('show');
                } else if (options.url != '') {
                    window.location.href = options.url;
                }
            }
        };
        ko.bindingHandlers.click.init(element, newValueAccesssor, allBindingsAccessor, viewModel, bindingContext);
    }
};


/*
 * Fused Autocomplete supports two versions of autocomplete (original autocomplete implementation by Jorn Zaefferer and jquery_ui)
 * Expects three parameters source, name and guid.
 * Ajax response lists needs name attribute.
 * Doco url: http://bassistance.de/jquery-plugins/jquery-plugin-autocomplete/
 * Note: Autocomplete implementation by Jorn Zaefferer is now been deprecated and its been migrated to jquery_ui.
 *
 */

ko.bindingHandlers.fusedAutocomplete = {

    init: function (element, params) {
        var params = params();
        var options = {};
        var url = ko.utils.unwrapObservable(params.source);
        options.source = function (request, response) {
            $(element).addClass("ac_loading");
            $.ajax({
                url: url,
                dataType: 'json',
                data: {q: request.term},
                success: function (data) {
                    var items = $.map(data.autoCompleteList, function (item) {
                        return {
                            label: item.name,
                            value: item.name,
                            source: item
                        }
                    });
                    response(items);

                },
                error: function () {
                    items = [{
                        label: "Error during species lookup",
                        value: request.term,
                        source: {listId: 'error-unmatched', name: request.term}
                    }];
                    response(items);
                },
                complete: function () {
                    $(element).removeClass("ac_loading");
                }
            });
        };
        options.select = function (event, ui) {
            var selectedItem = ui.item;
            params.name(selectedItem.source.name);
            params.guid(selectedItem.source.guid);
            params.scientificName(selectedItem.source.scientificName);
            params.commonName(selectedItem.source.commonName);
        };

        if (!$(element).autocomplete(options).data("ui-autocomplete")) {
            // Fall back mechanism to handle deprecated version of autocomplete.
            var options = {}, unknown = {
                guid: '',
                name: '(Unmatched taxon)',
                commonName: '',
                scientificName: '',
                value: element.value
            };
            options.source = url;
            options.matchSubset = false;
            options.formatItem = function (row, i, n) {
                return row.name;
            };
            options.highlight = false;
            options.parse = function (data) {
                var rows = new Array();
                if (params.matchUnknown) {
                    unknown.value = element.value;
                    unknown.name = element.value + ' (Unmatched taxon)'
                    rows.push({
                        data: unknown,
                        value: unknown,
                        result: unknown.name
                    })
                }
                data = data && data.resp && data.resp.autoCompleteList;
                for (var i = 0; i < data.length; i++) {
                    rows.push({
                        data: data[i],
                        value: data[i],
                        result: data[i].name
                    });
                }
                return rows;
            };

            $(element).autocomplete(options.source, options).result(function (event, data, formatted) {
                if (data) {
                    params.guid(data.guid);
                    params.name(data.name);
                    if (params.commonName && params.commonName != undefined) {
                        params.commonName(data.commonName);
                    }
                    if (params.scientificName && params.scientificName != undefined) {
                        params.scientificName(data.scientificName);
                    }
                }
            });
        }
    }
};
