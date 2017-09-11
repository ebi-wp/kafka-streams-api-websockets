var maxLabels = 60;

var config = {
    type: 'line',
    data: {
        labels: [],
        datasets: [{
            label: "OY",
            backgroundColor: window.chartColors.red,
            borderColor: window.chartColors.red,
            fill: false,
            data: []
        }, {
            label: "PG",
            backgroundColor: window.chartColors.blue,
            borderColor: window.chartColors.blue,
            fill: false,
            data: []
        }, {
            label: "HX",
            backgroundColor: window.chartColors.green,
            borderColor: window.chartColors.green,
            fill: false,
            data: []
        }, {
            label: "HH",
            backgroundColor: window.chartColors.orange,
            borderColor: window.chartColors.orange,
            fill: false,
            data: []
        }]
    },
    options: {
        responsive: true,
        title:{
            display:false,
            text:"Average wait time in each cluster"
        },
        scales: {
            xAxes: [{
                display: true,
                scaleLabel: {
                    display: false,
                    labelString: 'Timestamp'
                }
            }],
            yAxes: [{
                display: true,
                scaleLabel: {
                    display: true,
                    labelString: 'waiting time'
                }
            }]
        }
    }
};

window.onload = function() {
    var ctx = document.getElementById("canvas").getContext("2d");
    window.myLine = new Chart(ctx, config);

};


// Here I assume we receive messages in correct order
function addDataToChart(message){

    var splitMessage = message.split(" ");

    if (splitMessage.length == 4){
        var clusterName = splitMessage[0].toUpperCase();
        var timestamp = splitMessage[1];
        var count = splitMessage[2];
        var avg = splitMessage[3];

        // Add point if new
        if (config.data.labels.indexOf(timestamp) < 0)
            addOne(timestamp);

        // Remove one point if already too many
        if (config.data.labels.length > maxLabels)
            removeOne();

        // Update point
        config.data.datasets.forEach(function(dataset, datasetIndex) {
            if (dataset.label == clusterName){
                dataset.data.forEach(function(point, pointIdx) {
                    if (point.x == timestamp) point.y = avg;
                });
            }
        });

    } else {
        console.log(message);
    }
        window.myLine.update();
}


function addOne(timestamp) {

    config.data.labels.push(timestamp);
    config.data.datasets.forEach(function(dataset, datasetIndex) {
        dataset.data.push({x: timestamp, y: undefined});
    });

    window.myLine.update();
}

function removeOne() {

    config.data.labels.shift();
    config.data.datasets.forEach(function(dataset, datasetIndex) {
        dataset.data.shift();
    });

    window.myLine.update();
}