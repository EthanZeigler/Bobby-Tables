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
            url: 'http://localhost:9001/fields?state=' + $("#select_state").val() + "&county=" + $("#select_county").val() + "&disaster=" + $("#select_disaster").val() + "&category=" + $("#select_category").val() + "&category_name=" + $("#select_category_name").val(),
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
    var myChart = new Chart(ctx, {
        type: 'line',
        //data: 
        data: {
            labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
            datasets: [{
                label: '# of Votes',
                data: [12, 19, 3, 5, 2, 3],
                backgroundColor: [
                    'rgba(255, 99, 132, .2)',
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                ],
                borderWidth: 1
            }]
        },
        options: {
            title: {
                display: true,
                text: 'My Chart'
            },
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }],
                xAxes: [{
                    type: 'time',
                    time: {
                        displayFormats: {
                            quarter: 'MMM YYYY'
                        }
                    }
                }]
            },
            legend: {
                display: true,
                labels: {
                    fontColor: 'rgb(255, 99, 132)',
                    position: 'right'
                }
            },
            responsive: false
        }
    });
};