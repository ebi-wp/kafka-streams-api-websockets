package uk.ac.ebi.streaming;

import java.io.Serializable;

public class KafkaStreamingStatistics implements Serializable{

    long countJobs;
    long sumTime;
    double avgTime;

    public KafkaStreamingStatistics() {
    }

    public KafkaStreamingStatistics add(String jobRow){

        int jobTime = Integer.valueOf(jobRow);

        countJobs += 1;
        sumTime += jobTime;

        return this;
    }

    public KafkaStreamingStatistics computeAvgTime(){
        avgTime = sumTime / countJobs;
        return this;
    }

    @Override
    public String toString() {
        return countJobs + " " + avgTime;
    }
}
