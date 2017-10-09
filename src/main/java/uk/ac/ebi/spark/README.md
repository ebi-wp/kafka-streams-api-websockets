```
export SPARK_HOME=...

$SPARK_HOME/bin/spark-submit \
    --conf spark.driver.host=localhost \
    --class "uk.ac.ebi.spark.SparkConsume" \
    --master local[1] \
    target/shade-kafka-streaming-websockets-0.1.0.jar
```