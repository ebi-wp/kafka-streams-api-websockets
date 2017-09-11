var pgPoints = {};
var hxPoints = {};
var oyPoints = {};
var hhPoints = {};

var labels = {};

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
            data: [],
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

document.getElementById('randomizeData').addEventListener('click', function() {
    config.data.datasets.forEach(function(dataset) {
        dataset.data.forEach(function(dataObj) {
            dataObj.y = randomScalingFactor();
        });
    });

    window.myLine.update();
});

function addDataToChart(message){

    var splitMessage = message.split(" ");

    if (splitMessage.length == 4){
        var clusterName = splitMessage[0].toUpperCase();
        var timestamp = splitMessage[1];
        var count = splitMessage[2];
        var avg = splitMessage[3];
        // console.log(splitMessage);
        // TODO: add datapoint to chart
    } else {
        console.log(message);
    }
        window.myLine.update();
}

document.getElementById('addData').addEventListener('click', function() {
    if (config.data.datasets.length > 0) {
        var lastTick = config.data.labels.length;
        var newTick = lastTick + 1;
        console.log(newTick);

        config.data.labels.push(newTick.toString());

        for (var index = 0; index < config.data.datasets.length; ++index) {
            config.data.datasets[index].data.push({
                x: newTick.toString(),
                y: randomScalingFactor()
            });
        }

        window.myLine.update();
        console.log(config.data);
    }
});

document.getElementById('removeData').addEventListener('click', function() {
    config.data.datasets.forEach(function(dataset, datasetIndex) {
        dataset.data.pop();
    });

    window.myLine.update();
});