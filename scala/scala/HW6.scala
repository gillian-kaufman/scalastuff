// HOMEWORK 6
// dataset for this homework is customer-orders.csv

//in this homework, we will use sql function in the dataset to
//compute the total amount spent per customer in the e-commerce data
import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object HW6
{
  //declare case class called CustomerOrders
  //case class defines schema of dataset
  //each field of case class defines column name and column type of the dataset
  case class CustomerOrders( cust_id: Int, item_id: Int, amount_spent: Double)

  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)
    // use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("HW6").master("local[*]").getOrCreate()

    // Create schema using StructType object  when reading customer-orders.csv
    // since this file does not have header line
    val customerOrdersSchema = new StructType()
      .add("cust_id", IntegerType, nullable = true)
      .add("item_id", IntegerType, nullable = true)
      .add("amount_spent", DoubleType, nullable = true)

    // Load up the data into spark dataset
    // Use default separator (,), load schema from customerOrdersSchema and
    // force case class to read it as dataset
    import spark.implicits._
    val ds = spark.read.option("sep", ",")
      .schema(customerOrdersSchema)
      .csv("../Downloads/data_functional/data/customer-orders.csv")
      .as[CustomerOrders]
    //print ds schema
    ds.printSchema()

    //we will group by customer id and calculate total spent per customer id
    //hint sum() can be used to add things up
    //agg(round(sum(...),2) sums and rounds the result
    //sort by total_spent
    //show the complete results
    val ds2 = ds.groupBy("cust_id")
                .agg(round(sum("amount_spent"), 2)
                .alias("total_spent"))
    //ds2.show()
    val totalByCustomerSorted = ds2.sort("total_spent")

    totalByCustomerSorted.show()

    spark.stop()
  }
}
