import { Injectable } from '@angular/core';
import * as d3 from 'd3-3';
import { CommonService } from './common.service';
import { interval } from 'rxjs';
import { max } from 'rxjs/operators';
import { _ } from 'underscore';

@Injectable({
    providedIn: 'root'
})
export class ChartsService {
    ispopUp: Boolean = false;
    columNames: any = [];
    columnWithTypes: any = [];
    // graphType:string="Histogram";
    fontFamily: string = '';
    custom_width: number;
    custom_height: number;
    graph_width: any;
    color: string = 'url(#gradient-rainbow-main)';
    chartOptions: any = ["HorizontalBar", "VerticalBar"];
    units = ['thousands', 'millions', 'none'];
    storyIndex = 0;
    storyItemIndex = 0;
    id = "";

    constructor(public commonService: CommonService) { }

    //To select the type of graph
    findGraph(data, arrayLength) {
        let graphType,
            index,
            chartData;
        this.storyIndex = data.storyIndex;

        if (this.ispopUp) {
            graphType = this.commonService.cardsArray[arrayLength].graphType;
            chartData = data;
            this.id = "#image-1";
        }
        else {
            if (data.storyIndex == undefined) {
                graphType = this.commonService.cardsArray[arrayLength].graphType;
                chartData = data;
                this.id = "#image" + arrayLength;
            }
            else {
                graphType = data.graphType;
                chartData = data.sortedData;
                this.id = "#story" + data.storyIndex + arrayLength;
            }
        }

        index = arrayLength;

        switch (graphType) {
            case 'Interactive Bar':
                this.drawBrushedHorizontalBarChart(chartData, index);
                break;
            case 'Bar':
                this.drawHorizontalBarChart(chartData, index);
                break;
            case 'BubbleChart':
                this.drawBubbleChart(chartData, index);

                break;
            case 'Column':
                this.drawVerticalBar(chartData, index);
                break;
            case 'Line':
                this.drawLineChart(chartData, index);
                break;
            case 'Pie':
                this.drawPieChart(chartData, index);
                break;
            case 'Histogram':
                this.drawHistogram(chartData, index);
                break;
            case 'Stacked Bar':
                this.drawStackedBarChart(chartData, index);
                break;
            case 'Grouped Bar':
                this.drawGroupedBarChart(chartData, index);
                break;
            case 'scatter_plot':
                this.drawScatterPlot(chartData, index);
                break;
            case 'Multi Line Graph':
                this.drawMultiLineChart(chartData, index);
                break;
            default:
                this.drawBrushedHorizontalBarChart(chartData, index);

        }
    }

    //Function to draw brushed horizontal bar chart
    drawBrushedHorizontalBarChart(dataset, arrayLength) {
        var that = this;
        var svg,
            defs,
            gBrush,
            brush,
            main_xScale,
            mini_xScale,
            main_yScale,
            mini_yScale,
            main_yZoom,
            main_xAxis,
            main_yAxis,
            mini_width,
            textScale;
        let data = dataset;
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames,

            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";


        var color = this.commonService.cardsArray[arrayLength].color;
        // var appendId = "";
        // if (this.ispopUp) {
        //     appendId = "-1";
        // } else {
        //     appendId = arrayLength;
        // }
        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;
        // data.forEach((element, index) => {
        //     element.key = index;
        //     if(element[currentcolumNames[1]].replace(/,/g, "")!=undefined)
        //     element[currentcolumNames[1]]=element[currentcolumNames[1]].replace(/,/g, "");
        // });

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return Math.ceil((parseInt(v) / 1000000));


                case 'thousands': SIUnit = "K";
                    return Math.ceil((parseInt(v) / 1000));

                default: SIUnit = "";
                    return parseInt(v);
            }
        }

        // data.sort(function(a, b) {
        //     return b.value - a.value;
        // });

        /////////////////////////////////////////////////////////////
        ///////////////// Set-up SVG and wrappers ///////////////////
        /////////////////////////////////////////////////////////////

        //Added only for the mouse wheel
        var zoomer = d3.behavior.zoom()
            .on("zoom", null);

        var main_margin = {
            top: 10,
            right: 10,
            bottom: 30,
            left: 50
        },
            main_width = this.custom_width - main_margin.left - main_margin.right,
            main_height = this.custom_height + 95;

        var mini_margin = {
            top: 10,
            right: 10,
            bottom: 30,
            left: 10
        },
            mini_height = 400 - mini_margin.top - mini_margin.bottom;
        mini_width = 100 - mini_margin.left - mini_margin.right;

        // Define the div for the tooltip
        // var div = d3.select("body").append("div")
        //     .attr("class", "tooltip")
        //     .style("opacity", 0);

        function wrap(text, width) {
            text.each(function () {
                var text = d3.select(this),
                    words = text.text().split(/\s+/).reverse(),
                    word,
                    line = [],
                    lineNumber = 0,
                    lineHeight = 1.1, // ems
                    y = text.attr("y"),
                    dy = parseFloat(text.attr("dy")),
                    tspan = text.text(null).append("tspan").attr("x", 0).attr("y", y).attr("dy", dy + "em");
                while (word = words.pop()) {
                    line.push(word);
                    tspan.text(line.join(" "));
                    if (tspan.node().getComputedTextLength() > (width + 30)) {
                        line.pop();
                        tspan.text(line.join(" "));
                        line = [word];
                        tspan = text.append("tspan").attr("x", 0).attr("y", y).attr("dy", ++lineNumber * lineHeight + dy + "em").text(word);
                    }
                }
            });
        }
        let viewBox_height = 195, viewBox_width = 445;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }

        svg = d3.select(this.id).append("svg")
            .attr("class", "svgWrapper")
            .attr("viewBox", '0 0 ' + viewBox_width + ' ' + viewBox_height)
            .call(zoomer)
            .on("wheel.zoom", scroll)
            //.on("mousewheel.zoom", scroll)
            //.on("DOMMouseScroll.zoom", scroll)
            //.on("MozMousePixelScroll.zoom", scroll)
            //Is this needed?
            .on("mousedown.zoom", null)
            .on("touchstart.zoom", null)
            .on("touchmove.zoom", null)
            .on("touchend.zoom", null);

        var mainGroup = svg.append("g")
            .attr("class", "mainGroupWrapper" + this.id)
            .attr("transform", "translate(" + main_margin.left + "," + main_margin.top + ")")
            .append("g") //another one for the clip path - due to not wanting to clip the labels
            .attr("clip-path", "url(#clip)")
            .style("clip-path", "url(#clip)")
            .attr("class", "mainGroup" + this.id);

        var miniGroup = svg.append("g")
            .attr("class", "miniGroup" + this.id)
            .attr("transform", "translate(" + (main_margin.left + main_width + main_margin.right + mini_margin.left) + "," + mini_margin.top + ")");

        var brushGroup = svg.append("g")
            .attr("class", "brushGroup" + this.id)
            .attr("transform", "translate(" + (main_margin.left + main_width + main_margin.right + mini_margin.left) + "," + mini_margin.top + ")");


        var tooltip = d3.select("body").append("div").attr("class", "toolTip");
        /////////////////////////////////////////////////////////////
        ////////////////////// Initiate scales //////////////////////
        /////////////////////////////////////////////////////////////

        main_xScale = d3.scale.linear()
            .range([d3.min(data, function (d) {
                return (format(d[currentcolumNames[1]]) < 0 ? format(d[currentcolumNames[1]]) : 0)
            }), main_width
            ])
            .domain([d3.min(data, function (d) {
                return (format(d[currentcolumNames[1]]) < 0 ? format(d[currentcolumNames[1]]) : 0)
            }), d3.max(data, function (d) {
                return format(d[currentcolumNames[1]]);
            })]);

        mini_xScale = d3.scale.linear()
            .range([d3.min(data, function (d) {
                return (format(d[currentcolumNames[1]]) < 0 ? format(d[currentcolumNames[1]]) : 0)
            }), mini_width
            ])
            .domain([d3.min(data, function (d) {
                return (format(d[currentcolumNames[1]]) < 0 ? format(d[currentcolumNames[1]]) : 0)
            }), d3.max(data, function (d) {
                return format(d[currentcolumNames[1]]);
            })]);

        main_yScale = d3.scale.ordinal().rangeBands([0, main_height], 0.4, 0)
            .domain(data.map(function (d) {
                return d[currentcolumNames[0]];
            }));
        mini_yScale = d3.scale.ordinal().rangeBands([0, mini_height], 0.4, 0)
            .domain(data.map(function (d) {
                return d[currentcolumNames[0]];
            }));

        //Based on the idea from: http://stackoverflow.com/questions/21485339/d3-brushing-on-grouped-bar-chart
        main_yZoom = d3.scale.linear()
            .range([0, main_height])
            .domain([0, main_height]);

        main_xAxis = d3.svg.axis()
            .scale(main_xScale)
            .orient("bottom")
            .ticks(4);
        //.tickSize(0)
        // .outerTickSize(0);

        //Add group for the x axis
        d3.select(".mainGroupWrapper" + this.id)
            .append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(" + 0 + "," + (main_height + 5) + ")");

        //Create y axis object
        main_yAxis = d3.svg.axis()
            .scale(main_yScale)
            .orient("left")
            .tickSize(0)
            .outerTickSize(0);

        //Add group for the y axis
        mainGroup.append("g")
            .attr("class", "y axis")
            .attr("transform", "translate(-5,0)");

        /////////////////////////////////////////////////////////////
        /////////////////////// Update scales ///////////////////////
        /////////////////////////////////////////////////////////////

        //Update the scales
        // main_xScale.domain([0, d3.max(data, function(d) {
        //     return parseInt(d[currentcolumNames[1]]);
        // })]);
        // mini_xScale.domain([0, d3.max(data, function(d) {
        //     return parseInt(d[currentcolumNames[1]]);
        // })]);
        // main_yScale.domain(data.map(function(d) {
        //     return d[currentcolumNames[0]];
        // }));
        // mini_yScale.domain(data.map(function(d) {
        //     return d[currentcolumNames[0]];
        // }));

        //Create the visual part of the y axis
        d3.select(".mainGroup" + this.id).select(".y.axis").call(main_yAxis);

        /////////////////////////////////////////////////////////////
        ///////////////////// Label axis scales /////////////////////
        /////////////////////////////////////////////////////////////

        textScale = d3.scale.linear()
            .domain([15, 50])
            .range([12, 6])
            .clamp(true);

        /////////////////////////////////////////////////////////////
        ///////////////////////// Create brush //////////////////////
        /////////////////////////////////////////////////////////////

        //What should the first extent of the brush become - a bit arbitrary this
        // var brushExtent = Math.max( 1, Math.min( 20, Math.round(data.length*0.2) ) );
        var brushExtent = this.commonService.brushExtent;
        brush = d3.svg.brush()
            .y(mini_yScale)
            .extent([mini_yScale(data[0][currentcolumNames[0]]), mini_yScale(data[brushExtent][currentcolumNames[0]])])
            .on("brush", brushmove)
        //.on("brushend", brushend);

        //Set up the visual part of the brush
        gBrush = d3.select(".brushGroup" + this.id).append("g")
            .attr("class", "brush")
            .call(brush);

        gBrush.selectAll(".resize")
            .append("line")
            .attr("x2", mini_width);

        gBrush.selectAll(".resize")
            .append("path")
            .attr("d", d3.svg.symbol().type("triangle-up").size(20))
            .attr("transform", function (d, i) {
                return i ? "translate(" + (mini_width / 2) + "," + 4 + ") rotate(180)" : "translate(" + (mini_width / 2) + "," + -4 + ") rotate(0)";
            });

        gBrush.selectAll("rect")
            .attr("width", (mini_width));

        //On a click recenter the brush window
        gBrush.select(".background")
            .on("mousedown.brush", brushcenter)
            .on("touchstart.brush", brushcenter);

        ///////////////////////////////////////////////////////////////////////////
        /////////////////// Create a rainbow gradient - for fun ///////////////////
        ///////////////////////////////////////////////////////////////////////////

        defs = svg.append("defs")

        //Create two separate gradients for the main and mini bar - just because it looks fun
        createGradient("gradient-rainbow-main", "60%");
        createGradient("gradient-rainbow-mini", "13%");

        //Add the clip path for the main bar chart
        defs.append("clipPath")
            .attr("id", "clip")
            .append("rect")
            .attr("x", -main_margin.left)
            .attr("width", main_width)
            .attr("height", main_height);

        /////////////////////////////////////////////////////////////
        /////////////// Set-up the mini bar chart ///////////////////
        /////////////////////////////////////////////////////////////

        //The mini brushable bar
        //DATA JOIN
        var mini_bar = d3.select(".miniGroup" + this.id).selectAll(".bar")
            .data(data, function (d) {
                return d.key;
            });

        //UDPATE
        mini_bar
            .attr("width", function (d) {
                return mini_xScale((d[currentcolumNames[1]]));
            })
            .attr("y", function (d, i) {
                return mini_yScale(d[currentcolumNames[0]]);
            })
            .attr("height", mini_yScale.rangeBand());

        //ENTER
        mini_bar.enter().append("rect")
            .attr("class", "bar")
            .attr("x", 0)
            .attr("width", function (d) {
                return mini_xScale((format(d[currentcolumNames[1]])));
            })
            .attr("y", function (d, i) {
                return mini_yScale(d[currentcolumNames[0]]);
            })
            .attr("height", mini_yScale.rangeBand())
            .style("fill", color);

        //EXIT
        mini_bar.exit()
            .remove();

        //Start the brush
        gBrush.call(brush.event);

        d3.select('#image' + this.id).selectAll('text')
            .style('font-family', this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))

        // d3.selectAll('rect').on("mouseover", function(d) {
        //         div.transition()
        //             .duration(200)
        //             .style("opacity", .9);
        //         div.html(d[currentcolumNames[0]] + ',' + format(parseInt(d[currentcolumNames[1]]))+SIUnit)
        //             .style("left", (d3.event.pageX) + "px")
        //             .style("top", (d3.event.pageY - 20) + "px");
        //     })
        //     .on("mouseout", function(d) {
        //         div.transition()
        //             .duration(500)
        //             .style("opacity", 0);
        //     });
        // d3.selectAll('text').call(wrap, main_yScale.rangeBand());
        //Function runs on a brush move - to update the big bar chart

        function update() {

            /////////////////////////////////////////////////////////////
            ////////// Update the bars of the main bar chart ////////////
            /////////////////////////////////////////////////////////////

            //DATA JOIN
            var bar = d3.select(".mainGroup" + this.id).selectAll(".bar")
                .data(data, function (d) {
                    return d.key;
                });

            //UPDATE
            bar
                .attr("y", function (d, i) {
                    return main_yScale(d[currentcolumNames[0]]);
                })
                .attr("height", main_yScale.rangeBand())
                .attr("x", 0)
                .transition().duration(50)
                .attr("width", function (d) {
                    return main_xScale(((d[currentcolumNames[1]])));
                });

            //ENTER
            bar.enter().append("rect")
                .on("mousemove", function (d) {
                    tooltip
                        .style("left", d3.event.pageX - 50 + "px")
                        .style("top", d3.event.pageY - 70 + "px")
                        .style("display", "inline-block")
                        .html((d[currentcolumNames[0]]) + "<br>" + format(d[currentcolumNames[1]]) + SIUnit);
                })
                .on("mouseout", function (d) {
                    tooltip.style("display", "none");
                })
                .attr("class", "bar")
                .style("fill", color)
                .attr("y", function (d, i) {
                    return main_yScale(d[currentcolumNames[0]]);
                })
                .attr("height", main_yScale.rangeBand())
                .attr("x", 0)
                .transition().duration(50)
                .attr("width", function (d) {
                    return main_xScale(format(d[currentcolumNames[1]]));
                });

            d3.select(".mainGroup" + this.id).selectAll('text').call(wrap, main_yScale.rangeBand())
                .style('font-family', that.commonService.cardsArray[arrayLength].fontFamily)
                .style('font-size', (that.ispopUp) ? ((that.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (that.commonService.cardsArray[arrayLength].fontSize + "px"));

            //EXIT
            bar.exit()
                .remove();

        } //update

        /////////////////////////////////////////////////////////////
        ////////////////////// Brush functions //////////////////////
        /////////////////////////////////////////////////////////////

        //First function that runs on a brush move
        function brushmove() {

            var extent = brush.extent();

            //Which bars are still "selected"
            var selected = mini_yScale.domain()
                .filter(function (d) {
                    return (extent[0] - mini_yScale.rangeBand() + 1e-2 <= mini_yScale(d)) && (mini_yScale(d) <= extent[1] - 1e-2);
                });

            that.commonService.brushExtent = selected.length;
            //Update the colors of the mini chart - Make everything outside the brush grey
            d3.select(".miniGroup" + this.id).selectAll(".bar")
                .style("fill", function (d, i) {
                    return selected.indexOf(d[currentcolumNames[0]]) > -1 ? color : "#e0e0e0";
                });

            //Update the label size
            d3.selectAll(".y.axis text")
                .style("font-size", textScale(selected.length))
                .call(wrap);

            /////////////////////////////////////////////////////////////
            ///////////////////// Update the axes ///////////////////////
            /////////////////////////////////////////////////////////////

            //Reset the part that is visible on the big chart
            var originalRange = main_yZoom.range();
            main_yZoom.domain(extent);

            //Update the domain of the x & y scale of the big bar chart
            main_yScale.domain(data.map(function (d) {
                return d[currentcolumNames[0]];
            }));
            main_yScale.rangeBands([main_yZoom(originalRange[0]), main_yZoom(originalRange[1])], 0.4, 0);

            //Update the y axis of the big chart
            d3.select(".mainGroup" + this.id)
                .select(".y.axis")
                .call(main_yAxis);

            //Find the new max of the bars to update the x scale
            var newMaxXScale = d3.max(data, function (d) {
                return selected.indexOf(d[currentcolumNames[0]]) > -1 ? parseInt(d[currentcolumNames[1]]) : 0;
            });
            main_xScale.domain([0, newMaxXScale]);

            //Update the x axis of the big chart
            d3.select(".mainGroupWrapper" + this.id)
                .select(".x.axis")
                .call(main_xAxis)
                .transition().duration(50);


            //Update the big bar chart
            update();

        } //brushmove

        /////////////////////////////////////////////////////////////
        ////////////////////// Click functions //////////////////////
        /////////////////////////////////////////////////////////////

        //Based on http://bl.ocks.org/mbostock/6498000
        //What to do when the user clicks on another location along the brushable bar chart
        function brushcenter() {
            var target = d3.event.target,
                extent = brush.extent(),
                size = extent[1] - extent[0],
                range = mini_yScale.range(),
                y0 = d3.min(range) + size / 2,
                y1 = d3.max(range) + mini_yScale.rangeBand() - size / 2,
                center = Math.max(y0, Math.min(y1, d3.mouse(target)[1]));

            d3.event.stopPropagation();

            gBrush
                .call(brush.extent([center - size / 2, center + size / 2]))
                .call(brush.event);

        } //brushcenter

        /////////////////////////////////////////////////////////////
        ///////////////////// Scroll functions //////////////////////
        /////////////////////////////////////////////////////////////

        function scroll() {

            //Mouse scroll on the mini chart
            var extent = brush.extent(),
                size = extent[1] - extent[0],
                range = mini_yScale.range(),
                y0 = d3.min(range),
                y1 = d3.max(range) + mini_yScale.rangeBand(),
                dy = d3.event.deltaY,
                topSection;

            if (extent[0] - dy < y0) {
                topSection = y0;
            } else if (extent[1] - dy > y1) {
                topSection = y1 - size;
            } else {
                topSection = extent[0] - dy;
            }

            //Make sure the page doesn't scroll as well
            d3.event.stopPropagation();
            d3.event.preventDefault();

            gBrush
                .call(brush.extent([topSection, topSection + size]))
                .call(brush.event);
            // d3.selectAll('text').call(this.wrap, main_yScale.rangeBand());
        } //scroll

        /////////////////////////////////////////////////////////////
        ///////////////////// Helper functions //////////////////////
        /////////////////////////////////////////////////////////////

        //Create a gradient 
        function createGradient(idName, endPerc) {

            var coloursRainbow = ["#EFB605", "#E9A501", "#E48405", "#E34914", "#DE0D2B", "#CF003E", "#B90050", "#A30F65", "#8E297E", "#724097", "#4F54A8", "#296DA4", "#0C8B8C", "#0DA471", "#39B15E", "#7EB852"];

            defs.append("linearGradient")
                .attr("id", idName)
                .attr("gradientUnits", "userSpaceOnUse")
                .attr("x1", "0%").attr("y1", "0%")
                .attr("x2", endPerc).attr("y2", "0%")
                .selectAll("stop")
                .data(coloursRainbow)
                .enter().append("stop")
                .attr("offset", function (d, i) {
                    return i / (coloursRainbow.length - 1);
                })
                .attr("stop-color", function (d) {
                    return d;
                });
        } //createGradient

        //Function to generate random strings of 5 letters - for the demo only
        function makeWord() {
            var possible_UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            var text = possible_UC.charAt(Math.floor(Math.random() * possible_UC.length));

            var possible_LC = "abcdefghijklmnopqrstuvwxyz";

            for (var i = 0; i < 5; i++)
                text += possible_LC.charAt(Math.floor(Math.random() * possible_LC.length));

            return text;
        } //makeWord
    }

    //Function to draw horizontal bar chart
    drawHorizontalBarChart(dataset, arrayLength) {
        let data = dataset;
        // var columNames = this.commonService.cardsArray[arrayLength].columNames;
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;

        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;

        //set up svg using margin conventions - we'll need plenty of room on the left for labels
        var margin = {
            top: 15,
            right: 25,
            bottom: 15,
            left: 60
        };

        var height = this.custom_height,
            width = this.custom_width,
            barPadding = 5,
            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";

        var tooltip = d3.select("body").append("div").attr("class", "toolTip");

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return v;


                case 'thousands': SIUnit = "K";
                    return v;

                default: SIUnit = "";
                    return v;
            }
        }

        let viewBox_height = 195, viewBox_width = 445;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }
        var svg = d3.select(this.id).append("svg")
            .attr("viewBox", '0 0 ' + viewBox_width + ' ' + viewBox_height)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


        var x = d3.scale.linear()
            .range([d3.min(data, function (d) {
                return (format(d[currentcolumNames[1]]) < 0 ? format(d[currentcolumNames[1]]) : 0)
            }), width
            ])
            .domain([d3.min(data, function (d) {
                return (format(d[currentcolumNames[1]]) < 0 ? format(d[currentcolumNames[1]]) : 0)
            }), d3.max(data, function (d) {
                return format(d[currentcolumNames[1]]);
            })]);

        var y = d3.scale.ordinal()
            .rangeRoundBands([0, height], .1)
            .domain(data.map(function (d) {
                return d[currentcolumNames[0]];
            }));


        //make y axis to show bar names
        var yAxis = d3.svg.axis()
            .scale(y)
            //no tick marks
            .tickSize(0)
            .orient("left");

        var gy = svg.append("g")
            .attr("class", "y axis")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .call(yAxis);

        var bars = svg.selectAll(".bar")
            .data(data)
            .enter()
            .append("g")

        //append rects
        bars.append("rect")
            .on("mousemove", function (d) {
                tooltip
                    .style("left", d3.event.pageX - 50 + "px")
                    .style("top", d3.event.pageY - 70 + "px")
                    .style("display", "inline-block")
                    .html((d[currentcolumNames[0]]) + "<br>" + format(d[currentcolumNames[1]]) + SIUnit);
            })
            .on("mouseout", function (d) {
                tooltip.style("display", "none");
            })
            .attr("class", "bar")
            .style("fill", this.commonService.cardsArray[arrayLength].color)
            .attr("y", function (d) {
                return y(d[currentcolumNames[0]]);
            })
            .attr("height", y.rangeBand())
            .attr("x", 0)
            .attr("width", function (d) {
                return x(format(d[currentcolumNames[1]]));
            });

        //add a value label to the right of each bar
        bars.append("text")
            .attr("class", "label")
            //y position of the label is halfway down the bar
            .attr("y", function (d) {
                return y(d[currentcolumNames[0]]) + y.rangeBand() / 2 + 2;
            })
            //x position is 3 pixels to the right of the bar
            .attr("x", function (d) {
                return x(format(d[currentcolumNames[1]])) + 3;
            })
            .text(function (d) {
                return format(d[currentcolumNames[1]]) + SIUnit;
            })
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));

    }

    //Function to draw vertical bar chart
    drawVerticalBar(data, arrayLength) {
        let dataset = data;
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;


        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return v;


                case 'thousands': SIUnit = "K";
                    return v;

                default: SIUnit = "";
                    return v;
            }
        }

        var tooltip = d3.select("body").append("div").attr("class", "toolTip");
        // Dimensions for the chart: height, width, and space b/t the bars
        var margin = {
            top: 5,
            right: 25,
            bottom: 10,
            left: 60
        };

        var height = this.custom_height,
            width = this.custom_width,
            barPadding = 5,
            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";

        // Create a scale for the y-axis based on data
        // >> Domain - min and max values in the dataset
        // >> Range - physical range of the scale (reversed)
        var yScale = d3.scale.linear()
            .domain([0, d3.max(data, function (d) {
                return format(parseInt(d[currentcolumNames[1]]));
            })])
            .range([height, 0], .9);


        // Implements the scale as an actual axis
        // >> Orient - places the axis on the left of the graph
        // >> Ticks - number of points on the axis, automated
        var yAxis = d3.svg.axis()
            .scale(yScale)
            .orient('left')
            .ticks(5)
            .tickFormat(function (d) { return d + SIUnit });
        // .outerTickSize(4)


        // Creates a scale for the x-axis based on city names
        var xScale = d3.scale.ordinal()
            .domain(data.map(function (d) {
                return d[currentcolumNames[0]];
            }))
            .rangeBands([0, width]);

        // Creates an axis based off the xScale properties
        var xAxis = d3.svg.axis()
            .scale(xScale)
            .orient('bottom');

        let viewBox_height = 240, viewBox_width = 546;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }
        // Creates the initial space for the chart
        // >> Select - grabs the empty <div> above this script
        // >> Append - places an <svg> wrapper inside the div
        // >> Attr - applies our height & width values from above
        var chart = d3.select(this.id).append("svg")
            // .attr("viewBox", '0 0'+' ' + (width+2*margin.left+5*margin.right+1) +' '+ (height+margin.right+2*margin.right))
            .attr("viewBox", '0 0 ' + viewBox_width + ' ' + viewBox_height)
            .append('g')
            // .attr('transform', 'translate(' + (width+2*margin.left+5*margin.right+1)/4 + ',' + (margin.top + margin.right)+ ')');
            .attr('transform', 'translate(136.5, 30)');


        // For each value in our dataset, places and styles a bar on the chart

        // Step 1: selectAll.data.enter.append
        // >> Loops through the dataset and appends a rectangle for each value
        chart.selectAll('rect')
            .data(dataset)
            .enter()
            .append('rect').on("mousemove", function (d) {
                tooltip
                    .style("left", d3.event.pageX - 50 + "px")
                    .style("top", d3.event.pageY - 70 + "px")
                    .style("display", "inline-block")
                    .html((d[currentcolumNames[0]]) + "<br>" + format(parseInt(d[currentcolumNames[1]])) + SIUnit);
            })
            .on("mouseout", function (d) {
                tooltip.style("display", "none");
            })

            // Step 2: X & Y
            // >> X - Places the bars in horizontal order, based on number of
            //        points & the width of the chart
            // >> Y - Places vertically based on scale
            .attr('x', function (d, i) {
                return xScale(d[currentcolumNames[0]]);
            })
            .attr('y', function (d) {
                return yScale(format(parseInt(d[currentcolumNames[1]])));
            })

            // Step 3: Height & Width
            // >> Width - Based on barpadding and number of points in dataset
            // >> Height - Scale and height of the chart area
            .attr('width', (width / dataset.length) - barPadding)
            .attr('height', function (d) {
                return height - yScale(format(parseInt(d[currentcolumNames[1]])));
            })
            .attr('fill', this.commonService.cardsArray[arrayLength].color)

            // Step 4: Info for hover interaction
            .attr('class', function (d) {
                return d[currentcolumNames[0]];
            })
            .attr('id', function (d) {
                return format(parseInt(d[currentcolumNames[1]]));
            });

        // Renders the yAxis once the chart is finished
        // >> Moves it to the left 10 pixels so it doesn't overlap
        chart.append('g')
            .attr('class', 'axis')
            .attr('transform', 'translate(-10, 0)')
            .call(yAxis)
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));

        // Appends the xAxis
        chart.append('g')
            .attr('class', 'axis')
            .attr('transform', 'translate(0,' + (height + 10) + ')')
            .call(xAxis)
            .selectAll("text")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .style("text-anchor", "end")
            .attr("dx", "-.8em")
            .attr("dy", ".15em")
            .attr("transform", "rotate(-65)");


        // Adds yAxis title
        chart.append('text')
            .text(currentcolumNames[1])
            .attr('transform', 'translate(-40, -15)')
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));

    }

    //Function to draw line chart
    drawLineChart(dataset, arrayLength) {
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;

        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;

        // var label = d3.select("#image"+appendId);

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return v;

                case 'thousands': SIUnit = "K";
                    return v;
                default: SIUnit = "";
                    return v;
            }
        }


        // Set the dimensions of the canvas / graph
        var margin = {
            top: 15,
            right: 25,
            bottom: 15,
            left: 60
        };

        let height = this.custom_height,
            width = this.custom_width,
            barPadding = 5,
            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";
        let data = dataset;
        var tooltip = d3.select("body").append("div").attr("class", "toolTip");

        let viewBox_height = 185, viewBox_width = 445;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }

        var label = d3.select(".label");

        //Parse the date / time
        function getDate(d) {
            return new Date(d);
        }

        // Set the ranges
        // var	x = d3.time.scale().range([0, width])1;
        if (Date.parse(data[currentcolumNames[0][0]]) == NaN) {
            var x = d3.scale.ordinal()
                .domain(data.map(function (d) {
                    return d[currentcolumNames[0]];
                }))
                .rangeRoundBands([0, width], .5);
        }
        else {
            let sortedData = data;
            sortedData.sort((a, b) => new Date(b[currentcolumNames[0]]).getTime() - new Date(a[currentcolumNames[0]]).getTime())
            var maxDate = getDate(sortedData[0][currentcolumNames[0]]),
                minDate = getDate(sortedData[sortedData.length - 1][currentcolumNames[0]]);

            var x = d3.time.scale().domain([minDate, maxDate]).range([0, width]);
        }
        var y = d3.scale.linear().range([height, 0])
            .domain([0, d3.max(data, function (d) { return format(d[currentcolumNames[1]]); })]);

        // Define the axes
        var xAxis = d3.svg.axis().scale(x)
            .orient("bottom")
            .tickFormat(d3.time.format("%m-%d-%Y"));

        var yAxis = d3.svg.axis().scale(y)
            .orient("left")
            .ticks(5)
            .tickFormat(function (d) { return d + SIUnit });

        // Define the line
        var valueline = d3.svg.line()
            .x(function (d) { return x(getDate(d[currentcolumNames[0]])); })
            .y(function (d) { return y(format(d[currentcolumNames[1]])); });

        // Adds the svg canvas
        var svg = d3.select(this.id).append("svg")
            .attr("viewBox", '0 0 ' + viewBox_width + ' ' + viewBox_height)
            .append("g")
            .attr("transform", "translate(60,20)");


        // Add the valueline path.
        svg.append("path")		// Add the valueline path.
            .attr("class", "line")
            .attr("d", valueline(data))
            .attr('stroke', this.commonService.cardsArray[arrayLength].color)
            .attr('stroke-width', 2)
            .style('fill', 'none');

        // Add the valueline path.
        svg		// Add the valueline path.
            .selectAll("circle")
            .data(data)
            .enter()
            .append("circle")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .attr("r", 3)
            .attr("cx", function (d) {
                return x(getDate(d[currentcolumNames[0]]))
            })
            .attr("cy", function (d) {
                return y(format(d[currentcolumNames[1]]))
            })
            .attr('fill', this.commonService.cardsArray[arrayLength].color)
            .on("mousemove", function (d) {
                tooltip
                    .style("left", d3.event.pageX - 50 + "px")
                    .style("top", d3.event.pageY - 70 + "px")
                    .style("display", "inline-block")
                    .html(format(d[currentcolumNames[1]]) + " " + SIUnit + " " + d[currentcolumNames[0]]);
            })
            .on("mouseout", function (d) {
                tooltip.style("display", "none");
            })


        // Add the X Axis
        svg.append("g")			// Add the X Axis
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis)

            .selectAll("text")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .style("text-anchor", "end")
            .attr("dx", "-.8em")
            .attr("dy", ".15em")
            .attr("transform", "rotate(-65)");

        // Add the Y Axis
        svg.append("g")			// Add the Y Axis
            .attr("class", "y axis")
            .call(yAxis)
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));


    }

    //Function to draw pie chart
    drawPieChart(data, arrayLength) {
        var dataset = data;
        var color = this.commonService.cardsArray[arrayLength].color;


        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;
        // this.custom_width=document.getElementsByClassName('example-box')[0].clientWidth - 132;
        // this.custom_height=document.getElementsByClassName('example-box')[0].clientHeight - 60;

        var margin = {
            top: 15,
            right: 25,
            bottom: 15,
            left: 60
        };

        var r = (this.custom_height / 2),
            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";

        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;
        // currentcolumNames=Object.keys(data[0]);

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return v;


                case 'thousands': SIUnit = "K";
                    return v;

                default: SIUnit = "";
                    return v;
            }
        }
        var tooltip = d3.select("body").append("div").attr("class", "toolTip");
        // var data = [
        //     {"label":"Colorectale levermetastase (n=336)", "value":74}, 
        //     {"label": "Primaire maligne levertumor (n=56)", "value":12},
        //     {"label":"Levensmetatase van andere origine (n=32)", "value":7}, 
        //     {"label":"Beningne levertumor (n=34)", "value":7}
        // ];

        var colors = d3.scale.linear()
            .domain([0, data.length])
            .range(["#F5F5DC", this.commonService.cardsArray[arrayLength].color]);

        let viewBox_height = 197, viewBox_width = 445;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }

        var vis = d3.select(this.id).append("svg:svg")
            .data([data])
            .attr("viewBox", '0 0 ' + viewBox_width + ' ' + viewBox_height)
            .append("svg:g")
            .attr("transform", "translate(222.5,104)");
        // d3.select('svg').css({left: 200, position:'absolute'});

        var pie = d3.layout.pie().value(function (d) {
            return format(parseInt(d[currentcolumNames[1]]));
        });


        // Declare an arc generator function
        var arc = d3.svg.arc().outerRadius(r);

        // Select paths, use arc generator to draw
        var arcs = vis.selectAll("g.slice")
            .data(pie).enter()
            .append("svg:g")
            .on("mousemove", function (d) {
                tooltip
                    .style("left", d3.event.pageX - 50 + "px")
                    .style("top", d3.event.pageY - 70 + "px")
                    .style("display", "inline-block")
                    .html((d.data[currentcolumNames[0]]) + "<br>" + format(d.data[currentcolumNames[1]]) + SIUnit);
            })
            .on("mouseout", function (d) {
                tooltip.style("display", "none");
            }).attr("class", "slice");

        arcs.append("svg:path")
            .attr("fill", function (d, i) {
                return colors(i);
            })
            .attr("d", function (d) {
                return arc(d);
            });

        // Add the text
        arcs.append("svg:text")
            .attr("transform", function (d) {
                d.innerRadius = 100; /* Distance of label to the center*/
                d.outerRadius = r;
                return "translate(" + arc.centroid(d) + ")";
            })
            .attr("text-anchor", "middle")
            .text(function (d, i) {
                return (data[i][currentcolumNames[0]]);
            })
            .style("transform", 'rotate(180)')
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));
    }

    //Function to draw stacked bar chart
    drawStackedBarChart(data1, arrayLength) {
        var data = data1;
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;


        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;


        //set up svg using margin conventions - we'll need plenty of room on the left for labels
        var margin = {
            top: 5,
            right: 25,
            bottom: 10,
            left: 60
        };

        var height = this.custom_height,
            width = this.custom_width,
            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";
        let viewBox_width = width + 2 * margin.left + 5 * margin.right + 1, viewBox_height = height + margin.right + 2 * margin.right;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }
        var svg = d3.select(this.id)
            .append("svg")
            .attr("viewBox", '0 0' + ' ' + (viewBox_width) + ' ' + (viewBox_height))
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        var tooltip = d3.select("body").append("div").attr("class", "toolTip");

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return Math.ceil((parseInt(v) / 1000000));


                case 'thousands': SIUnit = "K";
                    return Math.ceil((parseInt(v) / 1000));

                default: SIUnit = "";
                    return parseInt(v);
            }
        }

        /* Data in strings like it would be if imported from a csv */

        //    data = [
        //     { year: "2006", redDelicious: "10", mcintosh: "15", oranges: "9", pears: "6" },
        //     { year: "2007", redDelicious: "12", mcintosh: "18", oranges: "9", pears: "4" },
        //     { year: "2008", redDelicious: "05", mcintosh: "20", oranges: "8", pears: "2" },
        //     { year: "2009", redDelicious: "01", mcintosh: "15", oranges: "5", pears: "4" },
        //     { year: "2010", redDelicious: "02", mcintosh: "10", oranges: "4", pears: "2" },
        //     { year: "2011", redDelicious: "03", mcintosh: "12", oranges: "6", pears: "3" },
        //     { year: "2012", redDelicious: "04", mcintosh: "15", oranges: "8", pears: "1" },
        //     { year: "2013", redDelicious: "06", mcintosh: "11", oranges: "9", pears: "4" },
        //     { year: "2014", redDelicious: "10", mcintosh: "13", oranges: "9", pears: "5" },
        //     { year: "2015", redDelicious: "16", mcintosh: "19", oranges: "6", pears: "9" },
        //     { year: "2016", redDelicious: "19", mcintosh: "17", oranges: "5", pears: "7" },
        //   ];

        //   var parse = d3.time.format("%Y").parse;
        var unstackedVariable = [];
        var stackedVariable = [];

        this.commonService.cardsArray[arrayLength].columnWithTypes.forEach(element => {
            if (element['dataType'] != 'int64')
                unstackedVariable.push(element['columnName']);
            else
                stackedVariable.push(element['columnName']);
        });

        // Transpose the data into layers
        var dataset = d3.layout.stack()(stackedVariable.map(function (stackedVariable) {
            return data.map(function (d) {
                return { x: d[unstackedVariable[0]], y: parseInt(d[stackedVariable]) };
            });
        }));

        // Set x, y and colors
        var x = d3.scale.ordinal()
            .domain(dataset[0].map(function (d) { return d.x; }))
            .rangeRoundBands([10, width - 10], 0.02);

        var y = d3.scale.linear()
            .domain([0, d3.max(dataset, function (d) {
                return d3.max(d, function (d) {
                    return format(parseInt(d.y0) + parseInt(d.y));
                });
            })])
            .range([height, 0]);

        var colors = d3.scale.linear()
            .domain([0, 9])
            .range([this.commonService.cardsArray[arrayLength].color, "green"]);


        // Define and draw axes
        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left")
        // .ticks(5)
        // .tickSize(-width, 0, 0)
        // .tickFormat( function(d) { return d } );

        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom")
        // .tickFormat(d3.time.format("%Y"));

        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .selectAll("text")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis)
            .selectAll("text")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .style("text-anchor", "end")
            .attr("dx", "-.8em")
            .attr("dy", ".15em")
            .attr("transform", "rotate(-65)");


        // Create groups for each series, rects for each segment 
        var groups = svg.selectAll("g.cost")
            .data(dataset)
            .enter().append("g")
            .attr("class", "cost")
            .style("fill", function (d, i) {
                return colors(i)
            });

        var rect = groups.selectAll("rect")
            .data(function (d) { return d; })
            .enter()
            .append("rect")
            .attr("x", function (d) { return x(d.x); })
            .attr("y", function (d) { return y(d.y0 + d.y); })
            .attr("height", function (d) {
                return y(d.y0) - y(d.y0 + d.y);
            })
            .attr("width", x.rangeBand())
            .on("mousemove", function (d) {
                tooltip
                    .style("left", d3.event.pageX - 50 + "px")
                    .style("top", d3.event.pageY - 70 + "px")
                    .style("display", "inline-block")
                    .html((d.x) + "<br>" + format(parseInt(d.y)) + SIUnit);
            })
            .on("mouseout", function (d) {
                tooltip.style("display", "none");
            })


        // Draw legend
        var legend = svg.selectAll(".legend")
            .data(stackedVariable)
            .enter().append("g")
            .attr("class", "legend")
            .attr("transform", function (d, i) {
                if (i <= d[i].length)
                    return "translate(30," + i * 19 + ")";
            });

        legend.append("rect")
            .attr("x", width - 18)
            .attr("width", 18)
            .attr("height", 18)
            .style("fill", function (d, i) {
                if (stackedVariable[i])
                    return colors(i);
            });

        legend.append("text")
            .attr("x", width + 5)
            .attr("y", 9)
            .attr("dy", ".35em")
            .style("text-anchor", "start")
            .text(function (d, i) {
                //   switch (i) {
                //     case 0: return "Anjou pears";
                //     case 1: return "Naval oranges";
                //     case 2: return "McIntosh apples";
                //     case 3: return "Red Delicious apples";
                //   }
                return stackedVariable[i]
            });
    }

    //Function to draw histogram
    drawHistogram(data1, arrayLength) {
        // add the histogram
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;

        // Get pre-computed histogram data
        var json = [
            {
                "name": "A",
                "data": [
                    {
                        "bin": 0,
                        "count": 30000
                    },
                    {
                        "bin": 10,
                        "count": 80000
                    },
                    {
                        "bin": 20,
                        "count": 180000
                    },
                    {
                        "bin": 30,
                        "count": 40000
                    },
                    {
                        "bin": 40,
                        "count": 40000
                    },
                    {
                        "bin": 50,
                        "count": 40000
                    }
                ]
            },
            {
                "name": "B",
                "data": [
                    {
                        "bin": 0,
                        "count": 90000
                    },
                    {
                        "bin": 10,
                        "count": 80000
                    },
                    {
                        "bin": 20,
                        "count": 10000
                    },
                    {
                        "bin": 30,
                        "count": 140000
                    },
                    {
                        "bin": 40,
                        "count": 140000
                    },
                    {
                        "bin": 50,
                        "count": 80000
                    }
                ]
            },
            {
                "name": "C",
                "data": [
                    {
                        "bin": 0,
                        "count": 10000
                    },
                    {
                        "bin": 10,
                        "count": 20000
                    },
                    {
                        "bin": 20,
                        "count": 120000
                    },
                    {
                        "bin": 30,
                        "count": 150000
                    },
                    {
                        "bin": 40,
                        "count": 130000
                    },
                    {
                        "bin": 50,
                        "count": 70000
                    }
                ]
            }
        ]

        var maxBin = 40;
        var binInc = 10;
        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return Math.ceil((parseInt(v) / 1000000));


                case 'thousands': SIUnit = "K";
                    return Math.ceil((parseInt(v) / 1000));

                default: SIUnit = "";
                    return parseInt(v);
            }
        }

        // transform data that is already binned into data
        // that is better for use in D3
        // we want to create something like this:
        // [
        //  { "x": 0,  "y": 30000 },
        //  { "x": 10, "y": 80000 },
        //  ...
        // ]
        // 
        for (var i = 0; i < json.length; i++) {

            // use the name of the group to initialize the array
            var group = json[i].name;
            var data = [];

            // we have a max bin for our histogram, must ensure
            // that any bins > maximum bin are rolled into the 
            // last bin that we have
            var binCounts = {};
            for (var j = 0; j < json[i].data.length; j++) {
                var xValue = json[i].data[j].bin;
                // bin cannot exceed the maximum bin
                xValue = (xValue > maxBin ? maxBin : xValue);
                var yValue = json[i].data[j].count;

                if (binCounts[xValue] === undefined) {
                    binCounts[xValue] = 0;
                }
                binCounts[xValue] += yValue;
            }

            // add the bin counts in
            for (var bin in binCounts) {
                data.push({ "x": bin, "y": binCounts[bin] });
            }


            d3.select(this.id).select("svg").remove();
            this.custom_width = 300;
            this.custom_height = 170;


            // A formatter for counts.
            var margin = {
                top: 5,
                right: 25,
                bottom: 10,
                left: 60
            };
            var formatCount = d3.format(",.0f");
            var height = this.custom_height,
                width = this.custom_width,
                barPadding = 5,
                unit = this.commonService.cardsArray[arrayLength].unit,
                SIUnit = "";

            let viewBox_width = width + 2 * margin.left + 5 * margin.right + 1,
                viewBox_height = height + margin.right + 2 * margin.right;
            if (this.commonService.storiesActive) {
                viewBox_height = 300;
                viewBox_width = 546;
            }

            var binArray = [];
            for (var i = 0; i <= maxBin + binInc; i += binInc) {
                binArray.push(i);
            }
            var binTicks = [];
            for (var i = 0; i < maxBin + binInc; i += binInc) {
                binTicks.push(i);
            }

            var x = d3.scale.linear()
                .domain([0, maxBin + binInc])
                .range([0, width]);

            // var binWidth = (parseFloat(width / (binArray.length - 1)) - 1);
            var binWidth = (width / (binArray.length - 1)) - 1;

            var y = d3.scale.linear()
                .domain([0, d3.max(data, function (d) { return format(d.y); })])
                .range([height, 0]);

            var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom")
                .tickValues(binTicks);

            var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");

            var svg = d3.select(this.id)
                .append('svg')
                .attr("viewBox", '0 0' + ' ' + (viewBox_width) + ' ' + (viewBox_height))
                .append('g')
                .attr('transform', 'translate(' + (width + 2 * margin.left + 5 * margin.right + 1) / 4 + ',' + 2 * (margin.top + margin.bottom) + ')');

            var bar = svg.selectAll(".bar")
                .data(data)
                .enter()
                .append("rect")
                .attr("class", "bar")
                .attr("x", function (d) { return x(d.x); })
                .attr("width", binWidth)
                .attr("y", function (d) { return y(format(d.y)); })
                .style("fill", this.commonService.cardsArray[arrayLength].color)
                .attr("height", function (d) { return height - y(format(d.y)); })
                .on("mouseover", function (d) {
                    var barWidth = parseFloat(d3.select(this).attr("width"));
                    var xPosition = parseFloat(d3.select(this).attr("x")) + (barWidth / 2);
                    var yPosition = parseFloat(d3.select(this).attr("y")) - 10;

                    svg.append("text")
                        .attr("id", "tooltip")
                        .attr("x", xPosition)
                        .attr("y", yPosition)
                        .attr("text-anchor", "middle")
                        .text(format(d.y) + SIUnit);
                })
                .on("mouseout", function (d) {
                    d3.select('#tooltip').remove();
                });

            svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
                .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
                .call(xAxis);

            svg.append("g")
                .attr("class", "y axis")
                .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
                .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
                //.attr("transform", "translate(0," + height + ")")
                .call(yAxis);

            // Add axis labels
            // svg.append("text")
            //     .attr("class", "x label")
            //     .attr("transform", "translate(" + (width / 2) + " ," + (height + margin.bottom - 15) + ")")
            //     //.attr("dy", "1em")
            //     .attr("text-anchor", "middle")
            //     .text("Time (minutes)");

            // svg.append("text")
            //     .attr("class", "y label")
            //     .attr("transform", "rotate(-90)")
            //     .attr("y", 0 - margin.left)
            //     .attr("x", 0 - (height / 2))
            //     .attr("dy", "1em")
            //     .attr("text-anchor", "middle")
            //     .text("Count");

            // Add title to chart
            // svg.append("text")
            //     .attr("class", "title")
            //     .attr("transform", "translate(" + (width / 2) + " ," + (-20) + ")")
            //     //.attr("dy", "1em")
            //     .attr("text-anchor", "middle")
            //     .text(group.toUpperCase());  
        };


    }



    //Function to draw grouped bar chart
    drawGroupedBarChart(data1, arrayLength) {
        var dataset = data1;
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;


        //To know if columnName is string for populating values dynamically
        // var currentcolumNames;
        // columNames.forEach(element => {
        //   if(element['dataType']=='String')
        //   currentcolumNames[0]=element['name'];
        // });

        //To remove commas as it blocks sorting
        // data.forEach(element => {
        //     element[currentcolumNames[1]]=element[currentcolumNames[1]].replace(/,/g, "");
        // });
        // console.log(columNames,columNames[arrayLength],columNames[arrayLength[1]])
        var tooltip = d3.select("body").append("div").attr("class", "toolTip");
        // Dimensions for the chart: height, width, and space b/t the bars
        var margin = {
            top: 5,
            right: 25,
            bottom: 10,
            left: 60
        };

        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;

        var height = this.custom_height,
            width = this.custom_width,
            barPadding = 5,
            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return Math.ceil((parseInt(v) / 1000000));


                case 'thousands': SIUnit = "K";
                    return Math.ceil((parseInt(v) / 1000));

                default: SIUnit = "";
                    return parseInt(v);
            }
        }

        var data = [
            {
                "categorie": "Student",
                "values": [
                    {
                        "value": 1000,
                        "rate": "Not at all"
                    },
                    {
                        "value": 4000,
                        "rate": "Not very much"
                    },
                    {
                        "value": 12000,
                        "rate": "Medium"
                    },
                    {
                        "value": 6,
                        "rate": "Very much"
                    },
                    {
                        "value": 0,
                        "rate": "Tremendously"
                    }
                ]
            },
            {
                "categorie": "Liberal Profession",
                "values": [
                    {
                        "value": 1000,
                        "rate": "Not at all"
                    },
                    {
                        "value": 21000,
                        "rate": "Not very much"
                    },
                    {
                        "value": 13000,
                        "rate": "Medium"
                    },
                    {
                        "value": 18000,
                        "rate": "Very much"
                    },
                    {
                        "value": 6000,
                        "rate": "Tremendously"
                    }
                ]
            },
            {
                "categorie": "Salaried Staff",
                "values": [
                    {
                        "value": 3000,
                        "rate": "Not at all"
                    },
                    {
                        "value": 22000,
                        "rate": "Not very much"
                    },
                    {
                        "value": 6000,
                        "rate": "Medium"
                    },
                    {
                        "value": 15000,
                        "rate": "Very much"
                    },
                    {
                        "value": 3000,
                        "rate": "Tremendously"
                    }
                ]
            },
            {
                "categorie": "Employee",
                "values": [
                    {
                        "value": 12000,
                        "rate": "Not at all"
                    },
                    {
                        "value": 7000,
                        "rate": "Not very much"
                    },
                    {
                        "value": 18000,
                        "rate": "Medium"
                    },
                    {
                        "value": 13000,
                        "rate": "Very much"
                    },
                    {
                        "value": 6000,
                        "rate": "Tremendously"
                    }
                ]
            },
            {
                "categorie": "Craftsman",
                "values": [
                    {
                        "value": 6000,
                        "rate": "Not at all"
                    },
                    {
                        "value": 15000,
                        "rate": "Not very much"
                    },
                    {
                        "value": 9000,
                        "rate": "Medium"
                    },
                    {
                        "value": 12000,
                        "rate": "Very much"
                    },
                    {
                        "value": 3000,
                        "rate": "Tremendously"
                    }
                ]
            },
            {
                "categorie": "Inactive",
                "values": [
                    {
                        "value": 6000,
                        "rate": "Not at all"
                    },
                    {
                        "value": 6000,
                        "rate": "Not very much"
                    },
                    {
                        "value": 6000,
                        "rate": "Medium"
                    },
                    {
                        "value": 2000,
                        "rate": "Very much"
                    },
                    {
                        "value": 3000,
                        "rate": "Tremendously"
                    }
                ]
            }
        ]


        var categoriesNames = data.map(function (d) { return d.categorie; });
        var rateNames = data[0].values.map(function (d) { return d.rate; });


        var x0 = d3.scale.ordinal()
            .rangeRoundBands([0, width], .1);

        var x1 = d3.scale.ordinal();

        var y = d3.scale.linear()
            .range([height, 0]);

        x0.domain(categoriesNames);
        x1.domain(rateNames).rangeRoundBands([0, x0.rangeBand()]);
        y.domain([0, d3.max(data, function (categorie) { return d3.max(categorie.values, function (d) { return format(d.value); }); })]);


        var xAxis = d3.svg.axis()
            .scale(x0)
            .tickSize(0)
            .orient("bottom");

        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left");

        var color = d3.scale.ordinal()
            .range(["#ca0020", "#f4a582", "#d5d5d5", "#92c5de", "#0571b0"]);

        let viewBox_height = height + margin.right + 2 * margin.right,
            viewBox_width = width + 2 * margin.left + 5 * margin.right + 1;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }
        var svg = d3.select(this.id)
            .append('svg')
            .attr("viewBox", '0 0' + ' ' + (viewBox_width) + ' ' + (viewBox_height))
            .append('g')
            .attr('transform', 'translate(' + (margin.left - 8) + ',' + (margin.top) + ')');


        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis)
            .selectAll("text")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .style("text-anchor", "end")
            .attr("dx", "-.8em")
            .attr("dy", ".15em")
            .attr("transform", "rotate(-65)");

        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .selectAll("text")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));

        svg.append("text")
            .attr("class", "y label")
            .attr("text-anchor", "end")
            .attr("y", 6)
            .attr("dy", "-5em")
            .attr("dx", "-4em")
            .attr("transform", "rotate(-90)")
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .style("font-weight", "unset")
            .text(currentcolumNames[1] + "(" + SIUnit + ")");

        var slice = svg.selectAll(".slice")
            .data(data)
            .enter().append("g")
            .attr("class", "g")
            .attr("transform", function (d) { return "translate(" + x0(d.categorie) + ",0)"; });

        slice.selectAll("rect")
            .data(function (d) { return d.values; })
            .enter().append("rect")
            .attr("width", x1.rangeBand())
            .attr("x", function (d) { return x1(d.rate); })
            .style("fill", function (d) { return color(d.rate) })
            .attr("y", function (d) { return y(0); })
            .attr("height", function (d) { return format(height - y(0)); })
            .on("mouseover", function (d) {
                tooltip
                    .style("left", d3.event.pageX - 50 + "px")
                    .style("top", d3.event.pageY - 70 + "px")
                    .style("display", "inline-block")
                    .html((d.rate) + "<br>" + format(d.value) + SIUnit);

                d3.select(this).style("fill", d3.rgb(color(d.rate)).darker(2));
            })
            .on("mouseout", function (d) {
                tooltip.style("display", "none");
                d3.select(this).style("fill", color(d.rate));
            });

        slice.selectAll("rect")
            .attr("y", function (d) { return y(format(d.value)); })
            .attr("height", function (d) { return height - y(format(d.value)); });

        var legend = svg.selectAll(".legend")
            .data(data[0].values.map(function (d) { return d.rate; }).reverse())
            .enter().append("g")
            .attr("class", "legend")
            .attr("transform", function (d, i) { return "translate(0," + i * 20 + ")"; })

        legend.append("rect")
            .attr("x", width + 130)
            .attr("width", 12)
            .attr("height", 12)
            .style("fill", function (d) { return color(d); });

        legend.append("text")
            .attr("x", width + 122)
            .attr("y", 9)
            .attr("dy", ".35em")
            .style("text-anchor", "end")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .text(function (d) { return d; });
    }

    //Function to draw bubble chart
    drawBubbleChart(data, arrayLength) {
        // set the dimensions and margins of the graph
        var data = data;
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;

        var color = this.color;

        var margin = {
            top: 10,
            right: 20,
            bottom: 30,
            left: 50
        },
            viewBox_height = 195, viewBox_width = 445;

        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }
        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;
        // append the svg object to the body of the page
        var svg = d3.select(this.id)
            .append("svg")
            .attr("viewBox", '0 0 ' + viewBox_width + ' ' + viewBox_height)
            .append("g")
            .attr("transform",
                "translate(" + margin.left + "," + margin.top + ")");

        //Read the data
        d3.csv("https://raw.githubusercontent.com/holtzy/data_to_viz/master/Example_dataset/4_ThreeNum.csv", function (data) {

            // Add X axis
            var x = d3.scale.linear()
                .domain([0, 12000])
                .range([0, this.custom_width]);
            // svg.append("g")
            // .attr("transform", "translate(0," + height + ")")
            // .call(d3.axisBottom(x));
            d3.svg.axis().scale(x)
                .orient("bottom")

            // Add Y axis
            var y = d3.scale.linear()
                .domain([35, 90])
                .range([this.custom_height, 0]);
            // svg.append("g")
            // .call(d3.axisLeft(y));
            d3.svg.axis().scale(y)
                .orient("left")

            // Add a scale for bubble size
            var z = d3.scale.linear()
                .domain([200000, 1310000000])
                .range([4, 40]);

            // Add a scale for bubble color
            var myColor = d3.scale.ordinal()
                .domain(["Asia", "Europe", "Americas", "Africa", "Oceania"])
                .range(d3.schemeSet2);

            // -1- Create a tooltip div that is hidden by default:
            var tooltip = d3.select("body")
                .append("div")
                .style("opacity", 0)
                .attr("class", "tooltip")
                .style("background-color", "black")
                .style("border-radius", "5px")
                .style("padding", "10px")
                .style("color", "white")

            // -2- Create 3 functions to show / update (when mouse move but stay on same circle) / hide the tooltip
            var showTooltip = function (d) {
                tooltip
                    .transition()
                    .duration(200)
                tooltip
                    .style("opacity", 1)
                    // .html("Country: " + d.country)
                    .style("left", (d3.mouse(this)[0] - 60) + "px")
                    .style("top", (d3.mouse(this)[1] + 40) + "px")
            }
            var moveTooltip = function (d) {
                tooltip
                    .style("left", (d3.mouse(this)[0] - 60) + "px")
                    .style("top", (d3.mouse(this)[1] + 40) + "px")
            }
            var hideTooltip = function (d) {
                tooltip
                    .transition()
                    .duration(200)
                    .style("opacity", 0)
            }

            // Add dots
            svg.append('g')
                .selectAll("dot")
                .data(data)
                .enter()
                .append("circle")
                .attr("class", "bubbles")
                .attr("cx", function (d) {
                    return x(d.gdpPercap);
                })
                .attr("cy", function (d) {
                    return y(d.lifeExp);
                })
                .attr("r", function (d) {
                    return z(d.pop);
                })
                .style("fill", function (d) {
                    return myColor(d.continent);
                })
                // -3- Trigger the functions
                .on("mouseover", showTooltip)
                .on("mousemove", moveTooltip)
                .on("mouseleave", hideTooltip)

        })
    }

    //Function to draw Scatterplot
    drawScatterPlot(data1, arrayLength) {
        var that = this;
        var data = [{
            "name": "Dairy Milk",
            "manufacturer": "cadbury",
            "price": 45,
            "rating": 2
        }, {
            "name": "Galaxy",
            "manufacturer": "Nestle",
            "price": 42,
            "rating": 3
        }, {
            "name": "Lindt",
            "manufacturer": "Lindt",
            "price": 80,
            "rating": 4
        }, {
            "name": "Hershey",
            "manufacturer": "Hershey",
            "price": 40,
            "rating": 1
        }, {
            "name": "Dolfin",
            "manufacturer": "Lindt",
            "price": 90,
            "rating": 5
        }, {
            "name": "Bournville",
            "manufacturer": "cadbury",
            "price": 70,
            "rating": 2
        }];
        // var data = data;
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;

        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;

        // just to have some space around items. 
        var margin = {
            top: 5,
            right: 25,
            bottom: 10,
            left: 60
        };

        var height = this.custom_height,
            width = this.custom_width,
            barPadding = 5,
            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";

        let viewBox_height = height + 2 * margin.right, viewBox_width = 520;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }

        var tooltip = d3.select("body").append("div").attr("class", "toolTip");

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return Math.ceil((parseInt(v) / 1000000));


                case 'thousands': SIUnit = "K";
                    return Math.ceil((parseInt(v) / 1000));

                default: SIUnit = "";
                    return parseInt(v);
            }
        }

        // this will be our colour scale. An Ordinal scale.
        var colors = d3.scale.category10();

        // we add the SVG component to the scatter-load div
        var svg = d3.select(this.id).append("svg")
            .attr("viewBox", '0 0' + ' ' + (viewBox_width) + ' ' + (viewBox_height))
            .append("g")
            .attr("transform", "translate(" + (width / 3) + "," + margin.bottom + ")");

        // this sets the scale that we're using for the X axis. 
        // the domain define the min and max variables to show. In this case, it's the min and max prices of items.
        // this is made a compact piece of code due to d3.extent which gives back the max and min of the price variable within the dataset
        var x = d3.scale.linear()
            .domain(d3.extent(data, function (d) {
                return d.price;
            }))
            // the range maps the domain to values from 0 to the width minus the left and right margins (used to space out the visualization)
            .range([0, width]);

        // this does the same as for the y axis but maps from the rating variable to the height to 0. 
        var y = d3.scale.linear()
            .domain(d3.extent(data, function (d) {
                return d.rating;
            }))
            // Note that height goes first due to the weird SVG coordinate system
            .range([height, 0]);

        // we add the axes SVG component. At this point, this is just a placeholder. The actual axis will be added in a bit
        svg.append("g").attr("class", "x axis").attr("transform", "translate(0," + y.range()[0] + ")");
        svg.append("g").attr("class", "y axis");

        // this is our X axis label. Nothing too special to see here.
        svg.append("text")
            .attr("fill", "#414241")
            .attr("text-anchor", "end")
            .attr("x", width / 2)
            .attr("y", height + 35)
            .text("Price in pence (£)")
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));



        // this is the actual definition of our x and y axes. The orientation refers to where the labels appear - for the x axis, below or above the line, and for the y axis, left or right of the line. Tick padding refers to how much space between the tick and the label. There are other parameters too - see https://github.com/mbostock/d3/wiki/SVG-Axes for more information
        var xAxis = d3.svg.axis().scale(x).orient("bottom").tickPadding(2);
        var yAxis = d3.svg.axis().scale(y).orient("left").tickPadding(2);

        // this is where we select the axis we created a few lines earlier. See how we select the axis item. in our svg we appended a g element with a x/y and axis class. To pull that back up, we do this svg select, then 'call' the appropriate axis object for rendering.    
        svg.selectAll("g.y.axis")
            .call(yAxis)
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));

        svg.selectAll("g.x.axis").call(xAxis)
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));


        // now, we can get down to the data part, and drawing stuff. We are telling D3 that all nodes (g elements with class node) will have data attached to them. The 'key' we use (to let D3 know the uniqueness of items) will be the name. Not usually a great key, but fine for this example.
        var chocolate = svg.selectAll("g.node").data(data, function (d) {
            return d.name;
        });

        // we 'enter' the data, making the SVG group (to contain a circle and text) with a class node. This corresponds with what we told the data it should be above.

        var chocolateGroup = chocolate.enter().append("g").attr("class", "node")
            // this is how we set the position of the items. Translate is an incredibly useful function for rotating and positioning items 
            .attr('transform', function (d) {
                return "translate(" + x(d.price) + "," + y(d.rating) + ")";
            });

        // we add our first graphics element! A circle! 
        chocolateGroup.append("circle")
            .attr("r", 5)
            .attr("class", "dot")
            .style("fill", function (d) {
                // remember the ordinal scales? We use the colors scale to get a colour for our manufacturer. Now each node will be coloured
                // by who makes the chocolate. 
                return colors(d.manufacturer);
            })
            .on("mousemove", function (d) {
                tooltip
                    .style("left", d3.event.pageX - 50 + "px")
                    .style("top", d3.event.pageY - 32 + "px")
                    .style("display", "inline-block")
                    .html((d.name));
            })
            .on("mouseout", function (d) {
                tooltip.style("display", "none");
            })
            .on("click", function () {
                that.commonService.graphColor = this.style.fill
            });

        // now we add some text, so we can see what each item is.
        // chocolateGroup.append("text")
        //     .style("text-anchor", "middle")
        //     .attr("dy", -10)
        //     .text(function (d) {
        //         // this shouldn't be a surprising statement.
        //         return d.name;
        // });
    }

    drawMultiLineChart(data1, arrayLength) {
        let currentcolumNames = this.commonService.cardsArray[arrayLength].columNames;

        d3.select(this.id).select("svg").remove();
        this.custom_width = 300;
        this.custom_height = 170;

        var margin = {
            top: 5,
            right: 25,
            bottom: 10,
            left: 60
        };

        var height = this.custom_height,
            width = this.custom_width,
            barPadding = 5,
            unit = this.commonService.cardsArray[arrayLength].unit,
            SIUnit = "";

        let viewBox_height = height + margin.right + 2 * margin.right, viewBox_width = width + 2 * margin.left + 5 * margin.right;
        if (this.commonService.storiesActive) {
            viewBox_height = 300;
            viewBox_width = 546;
        }

        function format(v) {

            switch (unit) {
                case 'millions': SIUnit = 'M';
                    return Math.ceil((parseInt(v) / 1000000));


                case 'thousands': SIUnit = "K";
                    return Math.ceil((parseInt(v) / 1000));

                default: SIUnit = "";
                    return parseInt(v);
            }
        }
        var data = [{
            "date": "20111001",
            "New York": "63.4",
            "San Francisco": "62.7",
            "Austin": "72.2"
        }, {
            "date": "20111002",
            "New York": "58.0",
            "San Francisco": "59.9",
            "Austin": "67.7"
        }, {
            "date": "20111003",
            "New York": "53.3",
            "San Francisco": "59.1",
            "Austin": "69.4"
        }];

        var parseDate = d3.time.format("%Y%m%d").parse;

        var x = d3.time.scale()
            .range([0, width]);

        var y = d3.scale.linear()
            .range([height, 0]);

        var color = d3.scale.category10();

        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");

        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left");

        var line = d3.svg.line()
            .interpolate("basis")
            .x(function (d) {
                return x(d.date);
            })
            .y(function (d) {
                return y(d.temperature);
            });

        var svg = d3.select(this.id)
            .append('svg')
            .attr("viewBox", '0 0' + ' ' + (viewBox_width) + ' ' + (viewBox_height))
            .append('g')
            .attr('transform', 'translate(' + (width) / 3 + ',' + margin.right + ')');

        color.domain(d3.keys(data[0]).filter(function (key) {
            return key !== "date";
        }));

        data.forEach(function (d) {
            d.date = parseDate(d.date);
        });

        var cities = color.domain().map(function (name) {
            return {
                name: name,
                values: data.map(function (d) {
                    return {
                        date: d.date,
                        temperature: +d[name]
                    };
                })
            };
        });

        x.domain(d3.extent(data, function (d) {
            return d.date;
        }));

        y.domain([
            d3.min(cities, function (c) {
                return d3.min(c.values, function (v) {
                    return v.temperature;
                });
            }),
            d3.max(cities, function (c) {
                return d3.max(c.values, function (v) {
                    return v.temperature;
                });
            })]);

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis)
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));

        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"))
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", "-5em")
            .style("text-anchor", "end")
            .text("Temperature (ºF)");

        var city = svg.selectAll(".city")
            .data(cities)
            .enter().append("g")
            .attr("class", "city");

        city.append("path")
            .attr("class", "line")
            .attr("d", function (d) {
                return line(d.values);
            })
            .style("stroke", function (d) {
                return color(d.name);
            })
            .attr('stroke-width', 2)
            .style('fill', 'none');

        city.append("text")
            .datum(function (d) {
                return {
                    name: d.name,
                    value: d.values[d.values.length - 1]
                };
            })
            .attr("transform", function (d) {
                return "translate(" + x(d.value.date) + "," + y(d.value.temperature) + ")";
            })
            .attr("x", 3)
            .attr("dy", ".35em")
            .text(function (d) {
                return d.name;
            })
            .style("font-family", this.commonService.cardsArray[arrayLength].fontFamily)
            .style('font-size', (this.ispopUp) ? ((this.commonService.cardsArray[arrayLength].fontSize - 4) + "px") : (this.commonService.cardsArray[arrayLength].fontSize + "px"));
    }
    //Function to re-render charts when we switch between components
    reRenderCharts(data) {
        data.forEach(async (element, index) => {
            this.ispopUp = false;
            let tableData=[];
            element.tabularData.row.forEach((element) => {
                let JsonObj;
                    for(let i=0;i<element.columnValues.length;i++)
                    {
                        if(JsonObj==undefined)
                        JsonObj={[element.columnValues[0].columnName]:element.columnValues[0].columnValue}
                        else
                        JsonObj[element.columnValues[i].columnName]=element.columnValues[i].columnValue;
                    }
                    tableData.push(JsonObj);
                    
            });
                    this.columNames = element.tabularData.columNames;
                    Object.values(tableData).map(ele => {
                        if (ele[this.columNames[1]] != undefined)
                            ele[this.columNames[1]] = this.format(parseInt(ele[this.columNames[1]].replace(/,/g, "")), element.unit, index);
                    })


                    if (element.sortObj && element.sortObj.direction == 'asc')
                        element.sortedData = (_.sortBy(tableData, element.sortObj.active));
                    else if (element.sortObj && element.sortObj.direction == 'desc')
                        element.sortedData = (_.sortBy(tableData, element.sortObj.active).reverse());
                    else
                        element.sortedData = (Object.values(tableData));

                    this.findGraph(element.sortedData, index);
                // let response = await this.commonService.formatTableDataPromise(element.tabularData)
                // if (response) {
                //     // console.log("response from api",response)
                //     this.columNames = element.tabularData.columNames;
                //     Object.values(response).map(ele => {
                //         if (ele[this.columNames[1]] != undefined)
                //             ele[this.columNames[1]] = this.format(parseInt(ele[this.columNames[1]].replace(/,/g, "")), element.unit, index);
                //     })


                //     if (element.sortObj && element.sortObj.direction == 'asc')
                //         element.sortedData = (_.sortBy(response, element.sortObj.active));
                //     else if (element.sortObj && element.sortObj.direction == 'desc')
                //         element.sortedData = (_.sortBy(response, element.sortObj.active).reverse());
                //     else
                //         element.sortedData = (Object.values(response));

                //     this.findGraph(element.sortedData, index);
                // }
                // else if (response['status'] == "error") {
                //     console.log(response['error']);
                // }
        });
    }
    format(v, unit, index) {

        switch (unit) {
            case 'millions': this.commonService.SIUnit[index] = 'M';
                return Math.ceil((parseInt(v) / 1000000));


            case 'thousands': this.commonService.SIUnit[index] = 'K';
                return Math.ceil((parseInt(v) / 1000));

            default:
                this.commonService.SIUnit[index] = '';
                return parseInt(v);
        }
    }
}