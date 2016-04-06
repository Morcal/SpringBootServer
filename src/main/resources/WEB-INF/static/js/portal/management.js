/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var Management = function () {};

    Management.prototype = {
        /** Internal tables. */
        tabs: [ 'session', 'activity' ],

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

            for (index = 0; index < this.tabs.length; index++) {
                tab = this.tabs[index];
                if (tab === target) {
                    $('#sidebar-' + tab).parent().addClass('active');
                    $('#management-' + tab).show();
                } else {
                    $('#sidebar-' + tab).parent().removeClass('active');
                    $('#management-' + tab).hide();
                }
            }

            this.current = target;

            switch (this.current) {
                case 'session':
                    return this.searchSessions('');
                case 'activity':
                    return this.searchActivities();
                default:
                    break;
            }

            throw new Error('unsupported tab.');
        },

        /**
         * Search activities.
         * @param query
         */
        searchActivities: function (query) {
            var that = this;

            $('#no-activity').hide();

            return $.portal.connector.request('search-activity', null, { query: query })
                .done(function (response) {
                    var activities, index, html,
                        table = $('#management-activity').find('table');

                    activities = response['activities'];
                    table.find('tbody').remove();
                    /* Update navigation badge. */
                    $('#sidebar-activity').find('span').html(activities.length);

                    if (!activities.length) {
                        $('#no-activity').show();
                        return;
                    }
                    html = '<tbody>';
                    for (index = 0; index < activities.length; index++) {
                        html += that.createActivityTableRow(activities[index]);
                    }
                    html += '</tbody>';

                    table.append(html);
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
                });
        },

        /**
         * Create NAS table row html.
         * @param activity activity.
         * @returns {string} html.
         */
        createActivityTableRow: function (activity) {
            var id = activity['id'],
                cls = activity['severity'].toLowerCase();

            switch (cls) {
                case 'error':
                    cls = 'danger';
                    break;

                case 'warn':
                    cls = 'warning';
                    break;

                case 'info':
                default:
                    cls = undefined;
                    break;
            }

            return '' +
                '<tr' + (cls ? ' class="' + cls + '"' : '') + '>' +
                '<td>' + id + '</td>' +
                '<td>' + activity['facility'] + '</td>' +
                '<td>' + activity['severity'] + '</td>' +
                '<td>' + activity['remote'] + '</td>' +
                '<td>' + activity['source'] + '</td>' +
                //'<td>' + activity['source_info'] + '</td>' +
                '<td>' + activity['action'] + '</td>' +
                '<td>' + new Date(activity['created']).toLocaleString() + '</td>' +
                //'<td>' + activity['result'] + '</td>' +
                '<td style="padding: 3px;">' +
                '<button type="button" class="btn btn-default" data-activity="' + id + '"' +
                ' onclick="$.portal.management.openActivity(this);" aria-label="Left Align">' +
                '<span class="glyphicon glyphicon-eye-open" aria-hidden="true" style="padding-right: 8px;"></span>' +
                'Detail</button>' +
                '</td>' +
                '</tr>';
        },

        /**
         * Open activity dialog.
         * @param e
         * @returns {*}
         */
        openActivity: function (e) {
            var act = $(e).data('activity');

            return $.portal.connector.request('get-activity', act)
                .done(function (response) {
                    var dialog = $('#activity-dialog'),
                        activity = response['activities'][0];

                    dialog.find(".modal-title").html('Activity ' + act + ' Detail');
                    dialog.find('#activity-facility').val(activity['facility']);
                    dialog.find('#activity-severity').val(activity['severity']);
                    dialog.find('#activity-remote').val(activity['remote']);
                    dialog.find('#activity-source').val(activity['source']);
                    dialog.find('#activity-sourceInfo').val(activity['source_info']);
                    dialog.find('#activity-action').val(activity['action']);
                    dialog.find('#activity-result').val(activity['result']);
                    dialog.find('#activity-date').val(new Date(activity['created']).toLocaleString());

                    dialog.modal('show');
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
                });
        },

        /**
         * Search sessions.
         * @param query
         * @returns {*}
         */
        searchSessions: function (query) {
            var that = this;

            $('#no-session').hide();

            return $.portal.connector.request('find-session', null, { query: query })
                .done(function (response) {
                    var sessions, index, html,
                        table = $('#management-session').find('table');

                    sessions = response['sessions'];
                    table.find('tbody').remove();
                    /* Update navigation badge. */
                    $('#sidebar-session').find('span').html(sessions.length);

                    if (!sessions.length) {
                        $('#no-session').show();
                        return;
                    }

                    html = '<tbody>';
                    for (index = 0; index < sessions.length; index++) {
                        html += that.createSessionTableRow(sessions[index]);
                    }
                    html += '</tbody>';

                    table.append(html);
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status ,err);
                });
        },

        /**
         * Create NAS table row html.
         * @param session session.
         * @returns {string} html.
         */
        createSessionTableRow: function (session) {
            var id = session['id'];

            return '' +
                '<tr>' +
                '<td>' + id + '</td>' +
                '<td>' + session['nas']['name'] + '</td>' +
                '<td>' + session['credentials']['username'] + '</td>' +
                '<td>' + session['credentials']['ip'] + '</td>' +
                '<td>' + session['credentials']['mac'] + '</td>' +
                '<td>' + new Date(session['start_date']).toLocaleString() + '</td>' +
                '<td style="padding: 3px;">' +
                '<button type="button" class="btn btn-danger" data-session="' + id + '"' +
                ' onclick="$.portal.management.deleteSession(this);" aria-label="Left Align">' +
                '<span class="glyphicon glyphicon-remove" aria-hidden="true" style="padding-right: 8px;"></span>Stop</button>' +
                '</td>' +
                '</tr>';
        },

        /**
         * Delete session.
         * @param e
         * @returns {*}
         */
        deleteSession: function (e) {
            $.application.load({
                function: this.deleteSessionInternal,
                args: [$(e).data('session')],
                object: this
            });
        },

        /**
         * Internal delete session.
         * @param session
         * @returns {*}
         */
        deleteSessionInternal: function (session) {
            var that = this;
            return $.portal.connector.request('disconnect', session, { args: [session]})
                .done(function () {
                    that.searchSessions($('#session-search').val());
                }).fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status, err);
                });
        }
    };

    $.portal.management = new Management();
}(jQuery));