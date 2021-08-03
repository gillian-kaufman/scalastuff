//In this lab, you will find the minimum temperature by weather station
import scala.math.min
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object FindMinTemperatues
{
  //This function will transform raw line data into tuple (stationID, entryType, Temperature)
  def parseLine(line: String): (String, String, Float) = {
    //split lines using comma
    val fields = line.split(",")
    //extract stationID and entryType
    val stationID = fields(0)
    val entryType = fields(2)
    //convert celsius to fahrenheit
    val temperature = fields(3).toFloat * 0.1f * (9.0f/5.0f) + 32.0f
    (stationID, entryType, temperature)
  }
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //create sparkcontext using every core of the local machine.
    val sc = new SparkContext("local[*]", "MinTemperatures")

    //Read each line of data
    val lines = sc.textFile("../Downloads/data_functional/data/1800.csv")

    //Convert each line to (stationID, entryType, temperature) tuples
    //using map function
    val parsedLines = lines.map(parseLine)

    //keep only TMIN entries using filter function
    val minTemps = parsedLines.filter(x => x._2 == "TMIN")

    //Convert (stationID, entryType, temperature) tuples to (stationID, temperature) tuple
    //you want to strip out all the data you can to minimize the amount of data that needs
    //to be pushed around during a shuffle operation
    val stationTemps = minTemps.map(x => (x._1, x._3.toFloat))

    //Find minimum temperature for each station using reduceByKey
    val minTempsByStation = stationTemps.reduceByKey((x, y) => min(x, y))

    //collect the results
    val results = minTempsByStation.collect()

    //format and print out results
    for(result <- results.sorted) {
      val station = result._1
      val temp = result._2
      val formattedTemp = f"$temp%.2f F"
      println(s"$station minimum temperature: $formattedTemp")
    }
    //Only have two weather stations
    //Output
    //Since two stations are pretty close, results are similar
    //EZE00100082 minimum temperature: 7.70 F
    //ITE00100554 minimum temperature: 5.36 F
  }
}

//Exercise
//find maximum temperature for whole years for each station

//Solution (I hope)
object FindMaxTemperatures
{
  //This function will transform raw line data into tuple (stationID, entryType, Temperature)
  def parseLine2(line: String): (String, String, Float) = {
    //split lines using comma
    val fields = line.split(",")
    //extract stationID and entryType
    val stationID = fields(0)
    val entryType = fields(2)
    //convert celsius to fahrenheit
    val temperature = fields(3).toFloat * 0.1f * (9.0f/5.0f) + 32.0f
    (stationID, entryType, temperature)
  }
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //create sparkcontext using every core of the local machine.
    val sc = new SparkContext("local[*]", "MinTemperatures")

    //Read each line of data
    val lines = sc.textFile("../Downloads/data_functional/data/1800.csv")

    //Convert each line to (stationID, entryType, temperature) tuples
    //using map function
    val parsedLines = lines.map(parseLine2)

    //keep only TMAX entries using filter function
    val maxTemps = parsedLines.filter(x => x._2 == "TMAX")

    //Convert (stationID, entryType, temperature) tuples to (stationID, temperature) tuple
    //you want to strip out all the data you can to minimize the amount of data that needs
    //to be pushed around during a shuffle operation
    val stationTemps = maxTemps.map(x => (x._1, x._3))

    //Find maximum temperature for each year for each station
    //collect the results
    val results = stationTemps.collect()

    //format and print out results
    for (result <- results.sorted) {
      val station = result._1
      val temp = result._2
      val formattedTemp = f"$temp%.2f F"
      println(s"$station maximum temperature: $formattedTemp")
    }
  }
}
