// takes selector id, keypair list comma delimited key=value pairs
function setSelectorContents(id, values) {
    $("#" + id).append(new Option("", ""));
    values.split(",").forEach(function(value) {
        $("#" + id).append(new Option(value.split("=")[0], value.split("=")[1]));
    });
}

function clearSelector(id) {
    $("#" + id).empty();
}

$(document).ready(function() {
    // TODO AJAX for new values
    clearSelector("select_county");
    clearSelector("select_disaster");
    clearSelector("select_category");
    clearSelector("select_category_name");
    $.ajax({
        url: 'http://localhost:9001/fields',
        dataType: 'application/json',
        complete: function (data) {
            console.log("complete")
            console.log(data);
            setSelectorContents("select_state", data.responseText);
        },
        error: function (data) {
            console.log(data);
        }
    });
});
$(function() {
    $("#select_state").on('change', function () {
        // TODO AJAX for new values
        clearSelector("select_county");
        clearSelector("select_disaster");
        clearSelector("select_category");
        clearSelector("select_category_name");
        $.ajax({
            url: 'http://localhost:9001/fields?state=' + $("#select_state").val(),
            dataType: 'application/json',
            complete: function (data) {
                console.log(data);
                setSelectorContents("select_county", data.responseText);
            }
        });
    });

    $("#select_county").on('change', function () {
        // TODO AJAX for new values
        clearSelector("select_disaster");
        clearSelector("select_category");
        clearSelector("select_category_name");
        $.ajax({
            url: 'http://localhost:9001/fields?state=' + $("#select_state").val() + "&county=" + $("#select_county").val(),
            dataType: 'application/json',
            complete: function (data) {
                console.log(data);
                setSelectorContents("select_disaster", data.responseText);
            }
        });
    });

    $("#select_disaster").on('change', function () {
        // TODO AJAX for new values
        clearSelector("select_category");
        clearSelector("select_category_name");
        $.ajax({
            url: 'http://localhost:9001/fields?state=' + $("#select_state").val() + "&county=" + $("#select_county").val() + "&disaster=" + $("#select_disaster").val(),
            dataType: 'application/json',
            complete: function (data) {
                console.log(data);
                setSelectorContents("select_category", data.responseText);
            }
        });
    });

    $("#select_category").on('change', function () {
        // TODO AJAX for new values
        clearSelector("select_category_name");
        $.ajax({
            url: 'http://localhost:9001/fields?state=' + $("#select_state").val() + "&county=" + $("#select_county").val() + "&disaster=" + $("#select_disaster").val() + "&category=" + $("#select_category").val(),
            dataType: 'application/json',
            complete: function (data) {
                console.log(data);
                setSelectorContents("select_category_name", data.responseText);
            }
        });
    });

    $("#button_apply").on('click', function () {
        // TODO AJAX for new values
        $.ajax({
            url: 'http://localhost:9001/data?state=' + $("#select_state").val() + "&county=" + $("#select_county").val() + "&disaster=" + $("#select_disaster").val() + "&category=" + $("#select_category").val() + "&category_name=" + $("#select_category_name").val(),
            dataType: 'application/json',
            complete: function (data) {
                $("#chart_container").html('<canvas id="myChart" class="col-auto w-75"></canvas>');
                loadChartJS(JSON.parse(data.responseText.split("!@##@!")[0]), JSON.parse(data.responseText.split("!@##@!")[1]));
            }
        });
    });
});

const verticalLinePlugin = {
    getLinePosition: function (chart, pointIndex) {
        const meta = chart.getDatasetMeta(0); // first dataset is used to discover X coordinate of a point
        const data = meta.data;
        return data[pointIndex]._model.x;
    },
    renderVerticalLine: function (chartInstance, pointIndex) {
        const lineLeftOffset = this.getLinePosition(chartInstance, pointIndex);
        const scale = chartInstance.scales['y-axis-0'];
        const context = chartInstance.chart.ctx;

        // render vertical line
        context.beginPath();
        context.strokeStyle = '#ff0000';
        context.moveTo(lineLeftOffset, scale.top);
        context.lineTo(lineLeftOffset, scale.bottom);
        context.stroke();

        // write label
        context.fillStyle = "#ff0000";
        context.textAlign = 'center';
        context.fillText('Disaster', lineLeftOffset, (scale.bottom - scale.top) / 2 + scale.top);
    },

    afterDatasetsDraw: function (chart, easing) {
        if (chart.config.lineAtIndex) {
            chart.config.lineAtIndex.forEach(pointIndex => this.renderVerticalLine(chart, pointIndex));
        }
    }
};
Chart.plugins.register(verticalLinePlugin);

function loadChartJS(dataBlock, settingsBlock) {
    var ctx = document.getElementById('myChart').getContext('2d');
    //var cape = ['58106', '58053', '58720', '59768', '60288', '60668', '61937'];
    //var jackson = ['19956', '19874', '20102', '20246', '20302', '20402', '20517'];
    //var rand = ['58106', '58053', '58720', '59768', '60288', '60668', '61937'];
    /*if ($("#select_state").val() == 'New Jersey') {
       datapoints = cape;
    } else if ($("#select_state").val() == 'Florida') {
        datapoints = jackson;
    } else {
        datapoints = rand;
    }*/
    var datapoints = 0;
    var myChart = new Chart(ctx, {
        type: 'line',
        data: {
         labels: ['2001', '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010','2011','2012','2013','2014', '2015','2016', '2017'],
            datasets: [{
                label: 'Numbers of Jobs',
                data: datapoints,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
        }]
    },
    options: {
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true
                }
            }]
        },
        responsive: false
    }
    });
};