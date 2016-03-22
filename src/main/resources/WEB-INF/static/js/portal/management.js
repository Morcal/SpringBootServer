/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var Management = function () {};

    Management.prototype = {
        searchActivity: function (query) {

        },

        /**
         * Search sessions.
         * @param query
         * @returns {*}
         */
        searchSessions: function (query) {
            var that = this;

            $('#no-session').hide();

            return $.portal.connector.request('find-session', { query: query })
                .done(function (response) {
                    var sessions, index, html,
                        table = $('#sessions').find('table');

                    sessions = response['sessions'];

                    if (!sessions.length) {
                        $('#no-session').show();
                        return;
                    }

                    /* Update navigation badge. */
                    $('#sidebar-session').find('span').html(sessions.length);

                    table.find('tbody').remove();
                    html = '<tbody>';
                    for (index = 0; index < sessions.length; index++) {
                        html += that.createSessionTableRow(sessions[index]);
                    }
                    html += '</tbody>';

                    table.append(html);
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
                '<td>' + session['start_time'] + '</td>' +
                '<td>' +
                '<button type="button" class="btn btn-btn-danger" data-session="' + id + '"' +
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
            var that = this;

            return $.portal.connector.request('delete-session', { id : e.data('session')})
                .done(function () {
                    that.searchSessions($('#session-search').val());
                });
        }
    };

    $.portal.management = new Management();
}(jQuery));