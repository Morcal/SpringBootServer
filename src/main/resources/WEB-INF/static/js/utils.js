/*jslint browser: true*/
/*global $ */
/*global jQuery */

(function ($) {
    'use strict';
    var utils = $.utils || {};

    $.extend(utils, {
        // Determine if given object is null
        isNull: function (value) {
            return value === undefined || value === null;
        },

        // Determine if give object is empty.
        isEmptyValue: function (value) {
            return this.isNull(value) || value === '';
        },

        // Determine if given control's value if empty.
        isEmpty: function (control) {
            return control && this.isEmptyValue(control.val());
        },

        // Determine if given jquery object has class matches name.
        hasClass: function (obj, name) {
            var i, classes = obj.attr('class').split(/\s+/);

            if (!name || this.isEmptyValue(name)) {
                return false;
            }
            for (i = 0; i < classes.length; i += 1) {
                if (classes[i] === name) {
                    return true;
                }
            }
        }
    });

    $.utils = utils;
}(jQuery));
