/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    var State = function () {},
        Connector = function (state) {
            this.state = state;
        };

    if (!$.portal)
        throw new Error('portal object not exists.');

    State.prototype = {
        authorized: function () {
            return this.nonce || this.token;
        },

        /**
         * Create HTTP Header authentication string.
         * @returns {*}
         */
        createAuth: function () {
            if (this.nonce) {
                return 'Digest ' + 'client_id="admin", nonce="' + encodeURI(this.nonce) + '"';
            } else {
                if (!this.token)
                    throw new Error('missing token!');
                return 'Digest ' + 'client_id="admin", admin_token="' + encodeURI(this.token) + '"';
            }
        }
    };

    Connector.prototype = {
        /**
         * Get api entry by action name.
         * @param action action name.
         * @returns {*}
         */
        entry: function (action) {
            var index, apis, entry;
            if (!action || $.utils.isEmptyValue(action))
                throw new Error('api entry action can not be empty.');

            if (!this.provider)
                throw new Error('provider not initialized.');

            apis = this.provider['registrations'][0]['apis'];

            for (index = 0; index < apis.length; index++) {
                entry = apis[index];
                if (entry && entry['action'] === action)
                    return entry;
            }

            $.logging.error('api entry {', action, '} not found.');
            throw new Error('api action {' + action + '} not found.');
        },

        /**
         * Try to authorize admin client and get challenge as response.
         * @returns {*}
         */
        authorize: function () {
            var that = this;
            return this.request('authorize', null, {
                response_type: 'challenge',
                scope: 'portal-system-admin',
                require_token: true,
                need_refresh_token: false
            }).done(function (response) {
                that.state.nonce = response['authentication']['nonce'];
                that.state.challenge = response['authentication']['challenge'];
            }).fail(function (xhr, status, err) {
                $.portal.ajaxError(xhr.responseText, status, err, 'authorize failed.');
            });
        },

        /**
         * Create connector.
         * @param url provider url.
         * @returns promise.
         */
        create: function (url) {
            var that = this, promise = $.get(url);

            promise.done(function (response) {
                $.logging.debug('connector created, response: ', response);
                that.provider = response;
            }).fail(function (response, status, err) {
                $.portal.ajaxError(response, status, err, 'create connector failed!');
            });

            return promise;
        },

        handleLogin: function (response) {
            delete this.state.challenge;
            delete this.state.nonce;
            this.state.token = response['authorization']['token'];
        },

        /**
         * Perform a REST action.
         * @param action action name.
         * @param path additional path adding to url.
         * @param data data if presents.
         * @param options ajax options.
         * @returns {*}
         */
        request: function (action, path, data, options) {
            var opt, url, entry, deferred = $.Deferred(), that = this,
                json = typeof options === 'string' && options == 'JSON';

            if (!this.provider || !this.state)
                throw new Error('connector not created yet.');

            data = data || {};

            if (json)
                data = JSON.stringify(data);

            options = options || {};
            entry = this.entry(action);

            if (!entry)
                return deferred.reject('api entry not found for:[' + action + '].').promise();

            opt = {
                type: entry['method'],
                data: data,
                options: options
            };

            /* Create url for requests. */
            url = entry['url'];

            if (path)
                url += '/' + path;

            if (entry['requires_auth']) {
                if (!this.state.authorized()) {
                    return deferred.reject({ responseText: 'not authorized'}).promise();
                }

                $.extend(opt, {
                    beforeSend: function (xhr) {
                        if (json)
                            xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
                        xhr.setRequestHeader('X-Xinli-Auth', that.state.createAuth());
                    }
                });
            }

            $.ajax(url, opt).done(function (response) {
                $.logging.debug('request result: ', JSON.stringify(response));
                deferred.resolve(response);
            }).fail(function (xhr, status, err) {
                deferred.reject(xhr, status, err);
            });

            return deferred.promise();
        }
    };

    $.portal.connector = new Connector(new State());
}(jQuery));