/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var Configuration = function () {
    };

    Configuration.prototype = {
        pages: ['nas', 'system'],
        init: function () {

        },

        switchTo: function (target) {
            var index;

            if (!target || target === this.current)
                return false;

            for (index = 0; index < this.pages.length; index++) {
                if (this.pages[index] === target) {
                    $('#configuration-' + this.pages[index]).addClass('active');
                } else {
                    $('#configuration-' + this.pages[index]).removeClass('active');
                }
            }

            this.current = target;
            return true;
        },

        /**
         * Search NAS devices.
         * @param query
         */
        searchNasDevices: function (query) {

        }
    };

    $.portal.configuration = new Configuration();
    $.portal.configuration.current = 'nas';
}(jQuery));
