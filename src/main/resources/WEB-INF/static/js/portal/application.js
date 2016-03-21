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
         * Display a loading spin when loading, after loading finished hide
         * the loading spin.
         *
         * @param runnable
         * @returns {*}
         */
        load: function (runnable) {
            var args, obj;

            if (!runnable || !runnable.function)
                throw new Error('no runnable.');

            args = runnable.args || {};
            obj = runnable.object || this;

            $('#loading').modal('show');

            return runnable.function.call(obj, args).done(function () {
                $('#loading').modal('hide');
            });
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
            promise.done(function (response) {
                $.logging.debug('login finished, result: ', response);
                modal.modal('hide');
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

            return true;
        },

        logout: function() {
            $.portal.connector.request('POST', '/portal/admin/logout', {});
        }

    };

    $.application = $.application || new Application();
    $.application.current = 'dashboard';
}(jQuery));