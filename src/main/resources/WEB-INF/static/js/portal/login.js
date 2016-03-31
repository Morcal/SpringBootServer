/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var Login = function () {};

    Login.prototype = {
        /**
         * Verfiy login username and password.
         * @param username username.
         * @param password password.
         * @returns {boolean} true if username and password provided and not empty.
         */
        verify: function (username, password) {
            if (!username || !password)
                throw new Error('username or password not found!');

            if ($.utils.isEmpty(username)) {
                username.focus();
                username.tooltip('show');
                e.preventDefault();
                return false;
            }

            if ($.utils.isEmpty(password)) {
                password.focus();
                password.tooltip('show');
                e.preventDefault();
                return false;
            }

            return true;
        },

        /**
         * Calculate md5 password with challenge.
         * @param password password.
         * @param challenge challenge.
         * @returns {*}
         */
        md5password: function (password, challenge) {
            if ($.utils.isEmptyValue(password) || $.utils.isEmptyValue(challenge)) {
                throw new Error('invalid password or challenge.');
            }

            return md5(password + challenge);
        },

        /**
         * Execute login.
         * @param username login username element.
         * @param password login password element.
         * @returns {*}
         */
        execute: function (username, password) {
            var deferred = $.Deferred(),
                promise,
                that = this;

            promise = $.portal.connector.authorize();

            promise.done(function () {
                $.logging.debug('logging in, user: ', username.val(), ', password: ', password.val());
                $.portal.connector.request('login', null, {
                    username: username.val(),
                    password: that.md5password(password.val(), $.portal.connector.state.challenge)
                }).done(function (response, status, xhr) {
                    $.portal.connector.handleLogin(response);
                    $.logging.debug('login handled by connector.');
                    deferred.resolve(response, status, xhr);
                }).fail(function (xhr, status, err) {
                    deferred.reject(xhr, status, err);
                });
            });

            return deferred.promise();
        }
    };

    $.portal.login = new Login();
}(jQuery));