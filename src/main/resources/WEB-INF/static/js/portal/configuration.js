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
            var that = this;
            return $.portal.connector.request('search-nas', {query: query})
                .done(function (response) {
                    var devices, index, html,
                        table = $('#configuration').find('table');

                    devices = response['devices'];

                    if (!devices.length)
                        return;

                    /* Update navigation badge. */
                    $('#configuration-nas').find('span').html(devices.length);

                    table.find('tbody').remove();
                    html = '<tbody>';
                    for (index = 0; index < devices.length; index++) {
                        html += that.createNasTableRow(devices[index]);
                    }
                    html += '</tbody>';

                    table.append(html);
                });
        },

        /**
         * Create NAS table row html.
         * @param device nas device.
         * @returns {string} html.
         */
        createNasTableRow: function (device) {
            var ip = device['ipv4_address'] || device['ipv6_address'] || 'unknown';
            return '' +
                '<tr>' +
                '<td>' + device['id'] + '</td>' +
                '<td>' + device['name'] + '</td>' +
                '<td>' + device['nas_type'] + '</td>' +
                '<td>' + ip + '</td>' +
                '<td>' +
                '<button type="button" class="btn btn-danger" data-nas="' + device['id'] + '"' +
                ' onclick="$.portal.configuration.openNas(this);" aria-label="Left Align">' +
                '<span class="glyphicon glyphicon-edit" aria-hidden="true" style="padding-right: 8px;"></span>Edit</button>' +
                '</td>' +
                '</tr>';
        },

        /**
         * Open NAS device detail in a modal dialog.
         * @param e button clicked.
         */
        openNas: function (e) {
            var nas = $(e).data('nas');

            $.portal.connector.request('get-nas', {id: nas})
                .done(function (response) {
                    var dialog = $('#nas-dialog'),
                        device = response['devices'][0];

                    dialog.find('div h4').html('NAS ' + device['id'] + ' Detail');
                    dialog.find('#nas-name').val(device['name']);
                    dialog.find('#nas-type').val(device['nas_type']);
                    dialog.find('#nas-ipv4').val(device['ipv4address']);
                    dialog.find('#nas-ipv6').val(device['ipv6address']);
                    dialog.find('#nas-auth-with-domain').val(device['authenticate_with_domain']);

                    dialog.modal('show');
                });
        },

        openTranslation: function (trans) {

        }
    };

    $.portal.configuration = new Configuration();
    $.portal.configuration.current = 'nas';
}(jQuery));
