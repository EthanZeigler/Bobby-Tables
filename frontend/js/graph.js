// takes selector id, keypair list comma delimited key=value pairs
function setSelectorContents(id, values) {
    values.split(",").forEach(function(value) {
        $("#" + id).append(new Option(value.split("=")[0], value.split("=")[1]));
    });
}

function clearSelector(id) {
    $("#" + id).empty();
}

$(document).ready(function () {
    // TODO AJAX for new values
    clearSelector("select_county");
    clearSelector("select_disaster");
    clearSelector("select_category");
    clearSelector("select_category_name");
    $.ajax({
        url: 'http://localhost:9000/fields?state=NJ',
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

$("#select_state").change(function () {
    // TODO AJAX for new values
    clearSelector("select_county");
    clearSelector("select_disaster");
    clearSelector("select_category");
    clearSelector("select_category_name");
    $.ajax({
        url: 'http://localhost:9000/fields?state=' + $("#select_state").val(),
        dataType: 'application/json',
        complete: function (data) {
            console.log(data);
            setSelectorContents("select_county", data.responseText);
        }
    });
});

$("#select_county").change(function () {
    // TODO AJAX for new values
    clearSelector("select_disaster");
    clearSelector("select_category");
    clearSelector("select_category_name");
    $.ajax({
        url: 'http://localhost:9000/fields?state=' + $("#select_state").val() + "&county=" + $("#select_county").val(),
        dataType: 'application/json',
        success: function (data) {
            setSelectorContents("select_disaster", data.responseText);
        }
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


var ctx = document.getElementById('myChart').getContext('2d');
var myChart = new Chart(ctx, {
    type: 'line',
    data: {
        labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
        datasets: [{
            label: '# of Votes',
            data: [12, 19, 3, 5, 2, 3],
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