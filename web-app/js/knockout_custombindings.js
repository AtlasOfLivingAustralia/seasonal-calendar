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