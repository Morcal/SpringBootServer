/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var Application = function () {};

    Application.prototype = {
        /** Internal modules. */
        pages: ['Dashboard', 'Configuration', 'Management'],

        /**
         * Initialize web application.
         */
        init: function () {
            this.load({
                function: $.portal.connector.create,
                object: $.portal.connector,
                args: '/portal/admin/api'
            }).done(function () {
                $('#login-dialog').modal('show');
            });
        },

        /**
         * Display alert message.
         * @param title
         * @param msg
         */
        alert: function (title, msg) {
            var html;

            if (msg) {
                msg = '<b>' + title + ':</b>&nbsp;' + msg;
            } else {
                msg = title;
            }

            html = '<div id="error-alert" class="alert alert-danger fade in" role="alert">' +
                '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
                '<span aria-hidden="true">&times;</span></button>' +
                '<p>' + msg + '</p>' +
                '</div>';

            $('.doc .container').prepend(html).alert();
        },

        /**
         * Display remote error.
         * @param response response.
         * @param status status.
         * @param err error.
         */
        displayRemoteError: function (response, status, err) {
            var ary = [];

            if (response) {
                ary.push($.utils.stringOf(response));
            }

            if (status)
                ary.push($.utils.stringOf(status));

            if (err)
                ary.push($.utils.stringOf(err));

            this.alert('Error', ary.join(', '));
        },

        /**
         * Display a loading spin when loading, after loading finished hide
         * the loading spin.
         *
         * @param runnable
         * @returns {*}
         */
        load: function (runnable) {
            var args, obj, loading;

            if (!runnable || !runnable.function)
                throw new Error('no runnable.');

            args = runnable.args || {};
            obj = runnable.object || this;

            loading = $('#loading-spin');
            loading.fadeIn();

            try {
                return runnable.function.call(obj, args).always(function () {
                    loading.fadeOut();
                });
            } catch (e) {
                this.alert(e.name, e.message);
            }
        },

        /**
         * Perform administration login.
         * @param modal login modal.
         * @returns {boolean}
         */
        login: function(modal) {
            var promise,
                login = $.portal.login,
                username = $(modal.find(':input[type=text]')[0]),
                password = $(modal.find(':password')[0]);

            if (!login.verify(username, password))
                return false;

            promise = login.execute(username, password);

            promise.done(function () {
                modal.modal('hide');

                $.logging.debug('creating dashboard.');
                $.portal.dashboard.create();
            }).fail(function (xhr, status, err) {
                $.application.displayRemoteError(xhr.responseText, status, err);
            });

            return true;
        },

        /**
         * Switch to a module with module name.
         * @param target module name.
         * @returns {boolean}
         */
        switchTo: function (target) {
            var index, module, toggle;

            if (!target || target === this.current)
                return false;

            for (index = 0; index < this.pages.length; index++) {
                module = this.pages[index].toLowerCase();
                if (this.pages[index] === target) {
                    $('#' + module).show();
                    $('#nav-'  + module).show();
                    $('#navbar-' + module).addClass('active');
                } else {
                    $('#' + module).hide();
                    $('#nav-'  + module).hide();
                    $('#navbar-' + module).removeClass('active');
                }
            }

            this.current = target;

            toggle = $('.navbar-toggle');
            if (toggle.is(':visible')) {
                $('.navbar-path').html(target);
                toggle.click();
            }

            if (target !== 'Dashboard')
                $.portal.dashboard.pause();
            else
                $.portal.dashboard.play();

            return true;
        },

        logout: function() {
            $.portal.connector.request('POST', '/portal/admin/logout', {});
        }

    };

    $.application = $.application || new Application();
    $.application.current = 'Dashboard';
}(jQuery));