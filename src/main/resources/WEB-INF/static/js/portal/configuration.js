/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var Configuration = function () {
    };

    Configuration.prototype = {
        /** Internal pages. */
        pages: ['nas', 'certificate', 'system', 'app'],

        /**
         * Initialize configuration.
         */
        init: function () {
            this.setupFileUpload('#ios-app-file', '#ios-app', 'ios');
            this.setupFileUpload('#android-app-file', '#android-app', 'android');
            this.setupFileUpload('#mac-app-file', '#mac-app', 'mac');
            this.setupFileUpload('#linux-app-file', '#linux-app', 'linux');
            this.setupFileUpload('#windows-app-file', '#windows-app', 'windows');
        },

        /**
         * Rest uploading.
         */
        resetLoading: function () {
            var loading = $('#loading');

            loading.find('div.modal-header>h4').html('Uploading...');
            loading.find('div.progress-bar').html('0%').css('width', '0%');
        },

        /**
         * Update uploading process.
         * @param progress
         */
        updateUploadProgress: function (progress) {
            var loading = $('#loading'),
                bar = $('#loading-progress-bar');

            if (!loading.is(':visible')) {
                loading.modal('show');
            }

            if (!bar.hasClass(':visible')) {
                bar.show();
            }

            bar.find('.progress-bar').css(
                'width',
                progress + '%'
            ).html(progress + '%');
        },

        /**
         * Set up file upload.
         * @param f element or element id.
         * @param input input text element.
         * @param os operation system name.
         */
        setupFileUpload: function (f, input, os) {
            var entry = $.portal.connector.entry('upload-app');

            $(f).fileupload({
                dataType: entry['response'].toLowerCase(),
                url: entry['url'] + '/' + os,
                paramName: 'file',
                add: function (e, data) {
                    data.formData = {
                        os: os,
                        filename: data.files[0].name
                    };
                    $.portal.configuration.resetLoading();
                    data.submit();
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    $.portal.configuration.updateUploadProgress(progress);
                },
                done: function (e, data) {
                    $(input).val(data.result['app']['filepath']);
                },
                fail: function () {
                    $.application.alert('Upload failed',
                        'Check if <code>apps</code> directory exists, upload file is large than limit or server disk is full.');
                },
                always: function () {
                    $('#loading').modal('hide');
                }
            });
        },

        /**
         * Delete app.
         * @param os
         */
        deleteApp: function (os) {
            $.application.load({
                function: $.portal.configuration.deleteAppInternal,
                object: $.portal.configuration,
                args: [os]
            });
        },

        /**
         * Internal delete app.
         * @param os
         * @returns {*}
         */
        deleteAppInternal: function (os) {
            return $.portal.connector.request('delete-app', os)
                .done(function () {
                    $('#' + os + '-app').val('');
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status, err);
                });
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
                case 'app':
                    return this.openAppConfiguration();
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
                        parent = $('#configuration-system');

                    parent.find('#private-key').val(config['private_key']);
                    parent.find('#server-allow-nat').prop('checked', config['allow_nat']);
                    parent.find('#server-check-redirect-url').prop('checked', config['check_redirect_url']);
                    parent.find('#enable-session-ttl').prop('checked', session['enable_ttl']);
                    parent.find('#session-ttl-value').val(session['ttl']);
                    parent.find('#session-token-ttl').val(session['token_ttl']);
                    parent.find('#enable-session-heartbeat').prop('checked', session['enable_heart_beat']);
                    parent.find('#session-heartbeat-interval').val(session['heart_beat_interval']);
                    parent.find('#portal-server-version').val(server['version']);
                    parent.find('#portal-server-listen-port').val(server['port']);
                    parent.find('#portal-server-shared-secret').val(server['shared_secret']);
                    parent.find('#enable-rate-limiting').prop('checked', config['enable_rate_limiting']);
                    parent.find('#activity-most-recent').val(activity['most_recent']);
                    parent.find('#min-activity-severity').val(activity['min_severity']);
                    parent.find('#enable-cluster').prop('checked', config['enable_cluster']);
                    parent.find('#cluster-redis-sentinels').val(config['cluster_sentinels']);
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
                });
        },

        /**
         * Open app configuration.
         * @returns {*}
         */
        openAppConfiguration: function () {
            return $.portal.connector.request('get-config')
                .done(function (response) {
                    var apps = response['server_configuration']['apps'],
                        parent = $('#configuration-app');

                    parent.find('#ios-app').val(apps['ios_app']);
                    parent.find('#android-app').val(apps['android_app']);
                    parent.find('#mac-app').val(apps['mac_app']);
                    parent.find('#linux-app').val(apps['linux_app']);
                    parent.find('#windows-app').val(apps['windows_app']);
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
            return $.portal.connector.request('search-nas', null, {query: query})
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
                '<td style="padding: 3px;">' +
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

            return $.portal.connector.request('get-nas', nas)
                .done(function (response) {
                    var dialog = $('#nas-dialog'),
                        device = response['devices'][0];

                    dialog.find('div h4').html('NAS ' + device['id'] + ' Detail');
                    dialog.find('#nas-id').val(device['id']);
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

        /**
         * Create new ans device.
         * @param nas
         * @returns {*}
         */
        createNas: function (nas) {
            return $.portal.connector.request('create-nas', null, nas, 'JSON');
        },

        /**
         * Update nas device.
         * @param nas
         * @returns {*}
         */
        saveNas: function (nas) {
            var id = $('#nas-id').val();
            return $.portal.connector.request('update-nas', id, nas, 'JSON');
        },

        openTranslationDialog: function () {
            var dialog = $('#nas-translation-dialog');

            dialog.find('#translation-modifier-target').val(0);
            dialog.find('#translation-modifier-position').val(0);
            dialog.find('#translation-modifier-value').val("");
            dialog.modal('show');
        },

        /**
         * Delete nas device.
         * @param id nas device id.
         * @returns {*}
         */
        deleteNas: function (id) {
            return $.portal.connector.request('delete-nas', id, { id: id });
        },

        /**
         * Open create nas device dialog.
         */
        createNasDialog: function () {
            var dialog = $('#nas-dialog');

            dialog.find('div h4').html('Create new NAS Device');
            dialog.find('input').val('');
            dialog.find('select').val(0);
            dialog.find('#delete-nas').prop('disabled', true);
            //dialog.find('#nas-open-translation').prop('disabled', true);
            dialog.modal('show');
        },

        /**
         * Handle nas type changed.
         * @param e
         */
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
            return $.portal.connector.request('search-certificate', null, {query: query})
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
            var id = certificate['id'],
                disabled = certificate['disabled'],
                cls = disabled ? 'danger' : '';

            return '' +
                '<tr class="' + cls + '">' +
                '<td>' + id + '</td>' +
                '<td>' + certificate['app_id'] + '</td>' +
                '<td>' + certificate['vendor'] + '</td>' +
                '<td>' + certificate['os'] + '</td>' +
                '<td>' + certificate['version'] + '</td>' +
                '<td>' + certificate['disabled'] + '</td>' +
                '<td style="padding: 3px;">' +
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

            return $.portal.connector.request('get-certificate', c)
                .done(function (response) {
                    var dialog = $('#certificate-dialog'),
                        secret = dialog.find('#certificate-shared-secret'),
                        certificate = response['certificates'][0];

                    dialog.find('div h4').html('Certificate ' + certificate['id']);
                    dialog.find('#create-certificate').hide();
                    dialog.find('#save-certificate').show();
                    dialog.find('input[type="hidden"]').val(certificate['id']);
                    dialog.find('#certificate-app-id').val(certificate['app_id']);
                    dialog.find('#certificate-vendor').val(certificate['vendor']);
                    dialog.find('#certificate-os').val(certificate['os']);
                    dialog.find('#certificate-version').val(certificate['version']);
                    dialog.find('#certificate-app-id').val(certificate['app_id']);
                    secret.val(certificate['shared_secret']);
                    secret.parent().show();

                    if (certificate['disabled']) {
                        dialog.find('#enable-certificate').show().prop('disabled', false);
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
        createCertificateDialog: function () {
            var dialog = $('#certificate-dialog');

            dialog.find('div h4').html('Create new certificate');
            dialog.find('input').val('');
            dialog.find('select').val(0);
            dialog.find('#create-certificate').show();
            dialog.find('#save-certificate').hide();
            dialog.find('#enable-certificate').show().prop('disabled', true);
            dialog.find('#disable-certificate').hide();
            dialog.find('#certificate-shared-secret').parent().hide();
            dialog.modal('show');
        },

        /**
         * Save server configuration.
         * @param key
         * @param value
         * @returns {*}
         */
        saveConfiguration: function (key, value) {
            return $.portal.connector.request('configure', null, {
                key: key,
                value: value
            });
        },

        /**
         * Create certificate.
         * @param app
         * @param vendor
         * @param os
         * @param version
         * @returns {*}
         */
        createCertificate: function (app, vendor, os, version) {
            return $.portal.connector.request('create-certificate', null, {
                app_id: app,
                vendor: vendor,
                os: os,
                version: version
            });
        },

        /**
         * Save(update) certificate.
         * @param certificate
         * @returns {*}
         */
        saveCertificate: function (certificate) {
            return $.portal.connector.request('update-certificate', certificate['id'], certificate, 'JSON');
        }
    };

    $.portal.configuration = new Configuration();
    $.portal.configuration.current = 'nas';
}(jQuery));
