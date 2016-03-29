/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    Chart.defaults.global.responsive = true;
    Chart.defaults.global.animation = false;
    //Chart.defaults.global.maintainAspectRatio = false;

    var Dashboard = function () {},
        TotalSessionChart = function () {},
        LoadChart = function () {};

    Dashboard.prototype = {
        create: function () {
            this.sessionChart = new TotalSessionChart();
            this.loadChart = new LoadChart();
            this.timer = $.timer({
                action: this.update,
                time: 3000,
                autostart: false
            });
            this.update();
        },

        pause: function () {
            this.timer.pause();
            $.logging.debug('dashboard timer paused.');
        },

        play: function () {
            this.timer.play(true);
            $.logging.debug('dashboard timer continue to play.');
        },

        update: function () {
            var dashboard = $.portal.dashboard;
            if (dashboard.firing)
                return;

            dashboard.timer.stop();

            $.logging.debug('updating...');

            dashboard.firing = true;

            $.portal.connector.request('get-statistics')
                .done(function (response) {
                    $('#session-summary').find('h1>code').html(response['total']['current']);
                    dashboard.updateDevices(response);
                    dashboard.updateLoadChart(response);
                    dashboard.updateTotalSessionChart(response);
                })
                .fail(function (xhr, status, err) {
                    $.application.displayRemoteError(xhr.responseText, status, err);
                })
                .always(function () {
                    dashboard.firing = false;
                    dashboard.timer.play(true);
                    $.logging.debug('restart timer.');
                });
        },

        updateDevices: function (response) {
            var index, device, html, devices = response['devices'],
                parent;

            $.logging.debug('updating devices...');

            parent = $('#devices-summary').children('div');
            parent.children().remove();

            for (index = 0; index < devices.length; index++) {
                device = devices[index];
                html = '<div class="col-md-3"><table class="table table-bordered table-striped">' +
                    '<thead><tr><th colspan="2">' + device['name'] + '</th></tr></thead>' +
                    '<tbody>' +
                    '<tr><td>total</td><td><code>' + device['total'] + '</code></td></tr>' +
                    '<tr><td>requests</td><td><code>' + device['requests'] + '</code></td></tr>' +
                    '<tr><td>errors</td><td><code>' + device['errors'] + '</code></td></tr>' +
                    '<tr><td>timeout</td><td><code>' + device['timeouts'] + '</code></td></tr>' +
                    '<tr><td>average response time</td><td><code>' + device['average_response_time'] + '</code></td></tr>' +
                    '</tbody></table>';
                parent.append(html);
                parent.append(html);
                parent.append(html);
                parent.append(html);
            }
        },

        updateTotalSessionChart: function (response) {
            var index, chartData, report = response['total']['report'],
                datasets = [],
                ds = report['datasets'];

            $.logging.debug('updating total sessions chart...');

            for (index = 0; index < ds.length; index++) {
                datasets.push({
                    fillColor: 'rgba(240, 240, 240, 0.2)',
                    strokeColor: 'rgba(144, 0, 0, 0.8)',
                    data: ds[index]['data']
                });
            }

            chartData = {
                labels: report['labels'],
                datasets: datasets
            };

            if (this.sessionChart.chart) {
                this.sessionChart.update(chartData);
            } else {
                this.sessionChart.create(chartData, {
                    scaleShowLabels: false,
                    maintainAspectRatio: false,
                    bezierCurve: false
                });
            }
        },

        updateLoadChart: function (response) {
            var index, chartData, report = response['load']['report'],
                datasets = [],
                ds = report['datasets'];

            $.logging.debug('updating system laod chart...');

            for (index = 0; index < ds.length; index++) {
                datasets.push({
                    fillColor: 'rgba(144, 0, 0, 0.8)',
                    strokeColor: 'rgba(144, 0, 0, 0.8)',
                    data: ds[index]['data']
                });
            }

            datasets.push();

            chartData = {
                labels: report['labels'],
                datasets: datasets
            };

            if (this.loadChart.chart) {
                this.loadChart.update(chartData);
            } else {
                this.loadChart.create(chartData, {
                    scaleShowLabels: false,
                    maintainAspectRatio: false,
                    bezierCurve: false
                });
            }
        }
    };

    TotalSessionChart.prototype = {
        create: function (data, option) {
            var ctx = $('#session-chart').get(0).getContext('2d');
            this.option = option;
            this.chart = new Chart(ctx).Line(data, option);
        },

        update: function (data) {
            this.chart.destroy();
            this.create(data, this.option);
        }
    };

    LoadChart.prototype =  {
        create: function (data, option) {
            var ctx = $('#load-chart').get(0).getContext('2d');
            this.option = option;
            this.chart = new Chart(ctx).Bar(data, option);
        },

        update: function (data) {
            this.chart.destroy();
            this.create(data, this.option);
        }
    };

    $.portal.dashboard = new Dashboard();
} (jQuery));