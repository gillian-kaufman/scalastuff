//Compute the total amount spent per customer in some e-commerce data.
// Sort the results based on amount spent

// text file name: customer-orders.csv
// format for each record: customerId itemID Moneyspent
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
object HW4
{
  // this is step by step instruction
  def parseLine(Line: String): (Int, Float) = {
    //Split each comma-separated line into fields
    val splitLine = Line.split(",")
    //Extract the customerID and Moneyspent fields and convert to int and float
    val customerID = splitLine(0).toInt
    val moneyspent = splitLine(2).toFloat
    //return key/value pairs key is age and value is numFriends
    (customerID, moneyspent)
  }
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //create sparkcontext using every core of the local machine.
    val sc = new SparkContext("local[*]", "TotalAmountPerCustomer")

    //Read each line of data
    val lines = sc.textFile("../Downloads/data_functional/data/customer-orders.csv")

    //map each line to key/value pairs of customer ID and dollar amount
    val parsedLines = lines.map(parseLine)

    //use reduceByKey to add up amount spent by customer ID
    val amountPerCustomer = parsedLines.reduceByKey((x, y) => x+y).sortByKey()

    //flip key (customer ID) value (amount spent)
    //collect results and print them
    val amountSorted = amountPerCustomer.map { case(customerID, moneyspent) => (moneyspent, customerID)}
      .collect()

    for (result <- amountSorted) {
      val moneyspent = result._1
      val formattedMoney = "$" + f"$moneyspent%.2f"
      val customerID = result._2
      println(s"Customer $customerID spent $formattedMoney")
    }
  }
}
