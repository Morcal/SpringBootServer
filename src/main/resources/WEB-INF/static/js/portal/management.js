/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var Management = function () {};

    Management.prototype = {
        searchActivity: function (query) {

        },

        searchSession: function (query) {
            var promise;

            query = query || {};
            promise = $.portal.connector.request('find-session', query);
            promise.done(function () {});
        }
    };

    $.portal.management = new Management();
}(jQuery));