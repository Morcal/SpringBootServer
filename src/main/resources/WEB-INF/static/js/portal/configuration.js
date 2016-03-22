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

        /**
         * Switch to configuration tab.
         * @param target target tab name.
         * @returns {promise}
         */
        switchTo: function (target) {
            var index, tab;

            if (!target)
                throw new Error('target tab is empty.');

            if (target === this.current)
                return $.Deferred().resolve().promise();

            for (index = 0; index < this.pages.length; index++) {
                tab = this.pages[index];
                if (tab === target) {
                    $('#sidebar-' + tab).parent().addClass('active');
                    $('#configuration-' + tab).show();
                } else {
                    $('#sidebar-' + tab).parent().removeClass('active');
                    $('#configuration-' + tab).hide();
                }
            }

            this.current = target;

            switch (this.current) {
                case 'nas':
                    return this.searchNasDevices('');
                case 'system':
                    return this.openSystemConfiguration();
                default:
                    break;
            }

            throw new Error('unsupported tab.');
        },

        /**
         * Open system configuration tab.
         * @returns {*}
         */
        openSystemConfiguration: function () {
            return $.portal.connector.request('configure')
                .done(function (response) {
                    var config = response['server_configuration'],
                        session = config['session'],
                        rest = config['rest'];

                    $.logging.debug('session configuration: ', session);
                    $.logging.debug('rest configuration: ', rest);
                });
        },

        /**
         * Search NAS devices.
         * @param query
         * @returns {promise}
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
                    $('#sidebar-nas').find('span').html(devices.length);

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
            var id, ip = device['ipv4_address'] || device['ipv6_address'] || 'unknown';

            id = device['id'];

            return '' +
                '<tr>' +
                '<td>' + id + '</td>' +
                '<td>' + device['name'] + '</td>' +
                '<td>' + device['nas_type'] + '</td>' +
                '<td>' + ip + '</td>' +
                '<td>' +
                '<button type="button" class="btn btn-default" data-nas="' + id + '"' +
                ' onclick="$.portal.configuration.openNas(this);" aria-label="Left Align">' +
                '<span class="glyphicon glyphicon-edit" aria-hidden="true" style="padding-right: 8px;"></span>Edit</button>' +
                '</td>' +
                '</tr>';
        },

        /**
         * Open NAS device detail in a modal dialog.
         * @param e button clicked.
         * @returns promise.
         */
        openNas: function (e) {
            var nas = $(e).data('nas');

            return $.portal.connector.request('get-nas', {id: nas})
                .done(function (response) {
                    var dialog = $('#nas-dialog'),
                        device = response['devices'][0];

                    dialog.find('div h4').html('NAS ' + device['id'] + ' Detail');
                    dialog.find('#nas-name').val(device['name']);
                    dialog.find('#nas-type').val(device['nas_type']);
                    dialog.find('#nas-ipv4').val(device['ipv4_address']);
                    dialog.find('#nas-ipv6').val(device['ipv6_address']);
                    dialog.find('#nas-auth-with-domain').val(device['authenticate_with_domain']);

                    if (device['nas_type'] === 'HUAWEI') {
                        dialog.find('#huawei-nas-shared-secret').val(device['portal_shared_secret']);
                        dialog.find('#huawei-nas-listen-port').val(device['listen_port']);
                        dialog.find('#huawei-nas-auth-type').val(device['authentication_type']);
                        dialog.find('#huawei-nas-version').val(device['version']);
                        dialog.find('div.huawei').show();
                    } else {
                        dialog.find('div.huawei').hide();
                    }
                    dialog.find('#delete-nas').prop('disabled', false);

                    dialog.modal('show');
                });
        },

        openTranslation: function (trans) {

        },

        deleteNas: function (nas) {
            $.portal.connector.request('remove-nas', { id: nas })
                .done(function () {

                });
        },

        createNas: function () {
            var dialog = $('#nas-dialog');

            dialog.find('div h4').html('Create new NAS Device');
            dialog.find('input').val('');
            dialog.find('select').val(0);
            dialog.find('#delete-nas').prop('disabled', true);
            dialog.modal('show');
        },

        changeNasType: function (e) {
            var value = $(e).val(),
                dialog = $('#nas-dialog');

            switch (value) {
                case 'HUAWEI':
                    dialog.find('div.huawei').show();
                    break;

                case 'CMCC':
                case 'RADIUS':
                default:
                    break;
            }
        }
    };

    $.portal.configuration = new Configuration();
    $.portal.configuration.current = 'nas';
}(jQuery));
