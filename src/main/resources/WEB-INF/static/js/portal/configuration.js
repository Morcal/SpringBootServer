/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var Configuration = function () {
    };

    Configuration.prototype = {
        pages: ['nas', 'certificate', 'system'],

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
                case 'certificate':
                    return this.searchCertificates('');
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
            return $.portal.connector.request('get-config')
                .done(function (response) {
                    var config = response['server_configuration'],
                        session = config['session'],
                        rest = config['rest'],
                        rate = config['rate_limiting'],
                        server = config['portal_server'],
                        activity = config['activity'],
                        dialog = $('#configuration-system');

                    dialog.find('#private-key').val(config['private_key']);
                    dialog.find('#server-allow-nat').val(config['allow_nat']);
                    dialog.find('#enable-session-ttl').prop('checked', session['enable_ttl']);
                    dialog.find('#session-ttl-value').val(session['ttl']);
                    dialog.find('#session-token-ttl').val(session['token_ttl']);
                    dialog.find('#enable-session-heartbeat').prop('checked', session['enable_heart_beat']);
                    dialog.find('#session-heartbeat-interval').val(session['heart_beat_interval']);
                    dialog.find('#portal-server-version').val(server['version']);
                    dialog.find('#portal-server-listen-port').val(server['port']);
                    dialog.find('#portal-server-shared-secret').val(server['shared_secret']);
                    dialog.find('#enable-rate-limiting').prop('checked', config['enable_rate_limiting']);
                    dialog.find('#activity-most-recent').val(activity['most_recent']);
                    dialog.find('#min-activity-severity').val(activity['min_severity']);
                    dialog.find('#enable-cluster').prop('checked', config['enable_cluster']);
                    dialog.find('#cluster-redis-sentinels').val(config['cluster_sentinels']);
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
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
                        table = $('#configuration-nas').find('table');

                    devices = response['devices'];
                    table.find('tbody').remove();
                    /* Update navigation badge. */
                    $('#sidebar-nas').find('span').html(devices.length);

                    if (!devices.length)
                        return;
                    html = '<tbody>';
                    for (index = 0; index < devices.length; index++) {
                        html += that.createNasTableRow(devices[index]);
                    }
                    html += '</tbody>';

                    table.append(html);
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
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
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
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
        },

        /**
         * Search certificates.
         * @param query
         * @returns {promise}
         */
        searchCertificates: function (query) {
            var that = this;
            return $.portal.connector.request('search-certificate', {query: query})
                .done(function (response) {
                    var certificates, index, html,
                        table = $('#configuration-certificate').find('table');

                    certificates = response['certificates'];
                    table.find('tbody').remove();
                    /* Update navigation badge. */
                    $('#sidebar-certificate').find('span').html(certificates.length);

                    if (!certificates.length)
                        return;
                    html = '<tbody>';
                    for (index = 0; index < certificates.length; index++) {
                        html += that.createCertificateTableRow(certificates[index]);
                    }
                    html += '</tbody>';

                    table.append(html);
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
                });
        },

        /**
         * Create NAS table row html.
         * @param certificate.
         * @returns {string} html.
         */
        createCertificateTableRow: function (certificate) {
            var id = certificate['id'];

            return '' +
                '<tr>' +
                '<td>' + id + '</td>' +
                '<td>' + certificate['app_id'] + '</td>' +
                '<td>' + certificate['vendor'] + '</td>' +
                '<td>' + certificate['os'] + '</td>' +
                '<td>' + certificate['version'] + '</td>' +
                '<td>' +
                '<button type="button" class="btn btn-default" data-certificate="' + id + '"' +
                ' onclick="$.portal.configuration.openCertificate(this);" aria-label="Left Align">' +
                '<span class="glyphicon glyphicon-edit" aria-hidden="true" style="padding-right: 8px;"></span>Edit</button>' +
                '</td>' +
                '</tr>';
        },

        /**
         * Open a certificate and show it in an open dialog if success.
         * @param e button which open this dialog.
         * @returns {*}
         */
        openCertificate: function (e) {
            var c = $(e).data('certificate');

            return $.portal.connector.request('get-certificate', { id: c })
                .done(function (response) {
                    var dialog = $('#certificate-dialog'),
                        certificate = response['certificates'][0];

                    dialog.find('div h4').html('Certificate ' + certificate['id']);
                    dialog.find('#certificate-app-id').val(certificate['app_id']);
                    dialog.find('#certificate-vendor').val(certificate['vendor']);
                    dialog.find('#certificate-os').val(certificate['os']);
                    dialog.find('#certificate-version').val(certificate['version']);
                    dialog.find('#certificate-app-id').val(certificate['app_id']);
                    dialog.find('#certificate-shared-secret').val(certificate['shared_secret']);

                    if (certificate['disabled']) {
                        dialog.find('#enable-certificate').show();
                        dialog.find('#disable-certificate').hide();
                    } else {
                        dialog.find('#enable-certificate').hide();
                        dialog.find('#disable-certificate').show();
                    }
                    dialog.modal('show');
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
                });
        },

        /**
         * Open create certificate dialog.
         * <p>Reset all input components and display 'enable' button.
         */
        createCertificate: function () {
            var dialog = $('#certificate-dialog');

            dialog.find('div h4').html('Create new certificate');
            dialog.find('input').val('');
            dialog.find('select').val(0);
            dialog.find('#enable-certificate').show().prop('disabled', true);
            dialog.find('#disable-certificate').hide();
            dialog.modal('show');
        },

        saveConfiguration: function (key, value) {
            return $.portal.connector.request('configure', {
                key: key,
                value: value
            });
        }
    };

    $.portal.configuration = new Configuration();
    $.portal.configuration.current = 'nas';
}(jQuery));
