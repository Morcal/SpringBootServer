/*jslint browser: true*/
/*global $ */
/*global jQuery */
(function ($) {
    'use strict';

    var SessionChart = function () {},
        NasSessionChart = function () {};

    SessionChart.prototype = {
        create: function () {
            var ctx;

            ctx = $('#session-chart').get(0).getContext('2d');

            this.chart = new Chart(ctx).Line({
                labels: ['7:00', '8:00', '9:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00', '22:00'],
                datasets: [{
                    fillColor: 'rgba(220, 220, 220, 0.2)',
                    data: [ 134, 869, 1134, 1324, 2011, 3132, 3213, 3400, 2100, 2010, 2120, 3230, 4532, 5464, 5562, 3244 ]
                }]
            }, {
                bezierCurve: false
            });
        },

        update: function(data) {
            if (!this.chart)
                return;

            data = {
                labels: ['17:00', '18:00', '19:00', '20:00', '21:00', '22:00'],
                datasets: [{
                    data: [ 2120, 3230, 4532, 5464, 5562, 3244 ]
                }]
            };

            this.chart.update(data, {
                bezierCurve: false
            });
        }
    };

    NasSessionChart.prototype =  {
        create: function () {
            var ctx;

            ctx = $('#nas-session-chart').get(0).getContext('2d');

            this.chart = new Chart(ctx).HorizontalBar({
                labels: ['nas-01', 'nas-02', 'nas-03', 'nas-04'],
                datasets: [{
                    fillColor: 'rgba(220, 220, 220, 0.2)',
                    data: [ 1530, 2182, 850, 1140  ]
                }]
            }, {
                bezierCurve: false
            });
        },

        update: function(data) {
            if (!this.chart)
                return;

            data = {
                labels: ['17:00', '18:00', '19:00', '20:00', '21:00', '22:00'],
                datasets: [{
                    data: [ 2120, 3230, 4532, 5464, 5562, 3244 ]
                }]
            };

            this.chart.update(data, {
                bezierCurve: false
            });
        }
    };

    $.portal.sessionChart = new SessionChart();
    $.portal.nasSessionChart = new NasSessionChart();
} (jQuery));