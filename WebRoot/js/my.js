$(function () {
    $('#container33').highcharts({
    	 chart: {
             plotBackgroundColor: null,
             plotBorderWidth: null,
             plotShadow: false
         },
         title: {
             text: '银行人员年龄段分布图, 2014'
         },
         tooltip: {
     	    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
         },
         plotOptions: {
             pie: {
                 allowPointSelect: true,
                 cursor: 'pointer',
                 dataLabels: {
                     enabled: true,
                     color: '#000000',
                     connectorColor: '#000000',
                     formatter: function() {
                         return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
                     }
                 }
             }
         },
         series: [{
             type: 'pie',
             name: '年龄段分布率',
             data: [
                 ['31-35岁',   45.0],
                 ['30岁以下',       26.8],
                 {
                     name: '36-40岁',
                     y: 12.8,
                     sliced: true,
                     selected: true
                 },
                 ['41-45岁',    8.5],
                 ['46-50岁',     6.2],
                 ['51岁以上（含）',   0.7]
             ]
         }]
     });
 });			

$(function () {
	
    $('#container34').highcharts({
	
    	 chart: {
 	        type: 'gauge',
 	        plotBackgroundColor: null,
 	        plotBackgroundImage: null,
 	        plotBorderWidth: 0,
 	        plotShadow: false
 	    },
 	    
 	    title: {
 	        text: '不良贷款率'
 	    },
 	    
 	    pane: {
 	        startAngle: -150,
 	        endAngle: 150,
 	        background: [{
 	            backgroundColor: {
 	                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
 	                stops: [
 	                    [0, '#FFF'],
 	                    [1, '#333']
 	                ]
 	            },
 	            borderWidth: 0,
 	            outerRadius: '109%'
 	        }, {
 	            backgroundColor: {
 	                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
 	                stops: [
 	                    [0, '#333'],
 	                    [1, '#FFF']
 	                ]
 	            },
 	            borderWidth: 1,
 	            outerRadius: '107%'
 	        }, {
 	            // default background
 	        }, {
 	            backgroundColor: '#DDD',
 	            borderWidth: 0,
 	            outerRadius: '105%',
 	            innerRadius: '103%'
 	        }]
 	    },
 	       
 	    // the value axis
 	    yAxis: {
 	        min: 0,
 	        max: 100,
 	        
 	        minorTickInterval: 'auto',
 	        minorTickWidth: 1,
 	        minorTickLength: 10,
 	        minorTickPosition: 'inside',
 	        minorTickColor: '#666',
 	
 	        tickPixelInterval: 30,
 	        tickWidth: 2,
 	        tickPosition: 'inside',
 	        tickLength: 10,
 	        tickColor: '#666',
 	        labels: {
 	            step: 2,
 	            rotation: 'auto'
 	        },
 	        title: {
 	            text: '实际值（%）'
 	        },
 	        plotBands: [{
 	            from: 0,
 	            to: 60,
 	            color: '#55BF3B' // green
 	        }, {
 	            from: 60,
 	            to: 80,
 	            color: '#DDDF0D' // yellow
 	        }, {
 	            from: 80,
 	            to: 100,
 	            color: '#DF5353' // red
 	        }]        
 	    },
 	
 	    series: [{
 	        name: '不良贷款率',
 	        data: [35],
 	        tooltip: {
 	            valueSuffix: ' %'
 	        }
 	    }]
 	
 	}, 
 	// Add some life
 	function (chart) {
 		if (!chart.renderer.forExport) {
 		    setInterval(function () {
 		        var point = chart.series[0].points[0],
 		            newVal,
 		            inc = Math.round((Math.random() - 0.5) * 20);
 		        
 		        newVal = point.y + inc;
 		        if (newVal < 0 || newVal > 150) {
 		            newVal = point.y - inc;
 		        }
 		        
 		        
 		    }, 3000);
 		}
 	});
 });							
$(function () {
    $('#container36').highcharts({
        chart: {
            type: 'line'
        },
        title: {
            text:'全行贷款新增量'
        },
        xAxis: {
            categories: ['2013-1', '2013-2', '2013-3', '2013-4', '2013-5', '2013-6', '2013-7', '2013-8', '2013-9', '2013-10', '2013-11', '2013-12']
        },
        yAxis: {
            title: {
                text: '贷款新增量 (百万元)'
            }
        },
        tooltip: {
            enabled: false,
            formatter: function() {
                return '<b>'+ this.series.name +'</b><br/>'+this.x +': '+ this.y +'百万元';
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true
                },
                enableMouseTracking: false
            }
        },
        series: [{
            name: '目标值',
            data: [7.0, 6.9, 9.5, 14.5, 18.4, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
        }, {
            name: '实际值',
            data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
        }]
    });
});			
$(function () {
    $('#container38').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: '各支行金额考核图'
        },
        subtitle: {
            text: '支行月度金额考核柱状图'
        },
        xAxis: {
            categories: [
                '一月份',
                '二月份',
                '三月份',
                '四月份',
                '五月份',
                '六月份',
                '七月份',
                '八月份',
                '九月份',
                '十月份',
                '十一月份',
                '十二月份'
            ]
        },
        yAxis: {
            min: 0,
            title: {
                text: '金额 (万元)'
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.1f} 万元</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: [{
            name: '楚店支行',
            data: [49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]

        }, {
            name: '城关支行',
            data: [83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3]

        }, {
            name: '城东支行',
            data: [48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2]

        }, {
            name: '单集支行',
            data: [42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1]

        }]
    });
});				
$(document).ready(function(){
	
    $('#container40').highcharts({
        data: {
            table: document.getElementById('datatable')
        },
        chart: {
            type: 'column'
        },
        title: {
            text: '岗位揽存新增量统计(万元)'
        },
        yAxis: {
            allowDecimals: false,
            title: {
                text: 'Units'
            }
        },
        tooltip: {
            formatter: function() {
                return '<b>'+ this.series.name +'</b><br/>'+
                    this.y +' '+ this.x;
            }
        }
    });
});		
$(function () {
    $('#container42').highcharts({
        chart: {
            zoomType: 'xy'
        },
        title: {
            text: '全行贷款新增量(万元)'
        },
        
        xAxis: [{
            categories: ['一月', '二月', '三月', '四月', '五月', '六月',
                '七月', '八月', '九月', '十月', '十一月', '十二月']
        }],
        yAxis: [ { // Secondary yAxis
            title: {
                text: '',
                style: {
                    color: '#4572A7'
                }
            },
            labels: {
                format: '{value} %',
                style: {
                    color: '#4572A7'
                }
            },
            opposite: true
        },{ // Primary yAxis
            labels: {
                format: '{value}万元',
                style: {
                    color: '#89A54E'
                }
            },
            title: {
                text: '贷款新增量',
                style: {
                    color: '#89A54E'
                }
            }
        }],
        tooltip: {
            shared: true
        },
        legend: {
           
            backgroundColor: '#FFFFFF'
        },
        series: [{
            name: '贷款新增量',
            color: '#4572A7',
            type: 'column',
            yAxis: 1,
            data: [15000,	18000,	21000	,12000,	14000	,21000	,22000	,21000	,14000	,18000,	13000,	21000],
            tooltip: {
                valueSuffix: ' 万元'
            }

        }, {
            name: '同比（%）',
            color: '#b22222',
            type: 'spline',
            data: [20,	90	,80	,110	,120,	130,	80,	90	,80	,120	,70,	140],
            tooltip: {
                valueSuffix: '%'
            }
        },{
            name: '环比（%）',
            color: '#EE7621',
            type: 'spline',
            data: [80	,120.00 	,116.67 ,	57.14 	,116.67 ,	150.00 ,	104.76 ,	95.45 ,	66.67 ,	128.57 	,72.22 	,161.54 ],
            tooltip: {
                valueSuffix: '%'
            }
        }]
    });
});