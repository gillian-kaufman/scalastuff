object Ch1
{
  //main function ia where you start your application
  def main(args: Array[String]): Unit =
  {
    //standard way to create scala variable
    //you dont need to specify type of variable
    //type information is inferred by compilerQ
    val answer = 8 * 5 + 2

    //if you want to be more defensive you can give type info
    //however this is rarely used
    var answer1: Int = 8 * 5 + 2
    /* Loading types during compiling time as opposed to runtime allows for error checking instead of just crashing at runtime*/

    //a value declared with val is actually a constant
    //you cant change its contents meaning its immutable
    //answer = 10

    //to declare a variable whose contents can vary use var
    var counter = 0
    counter = 1 //ok to change a var

    //in other words var is mutable variable val is immutable
    println("counter: " + counter)

    /* In Scala, you are encouraged to use a val unless you really need to
    * change the contents of a variable. In other words, immutability is encouraged. */

    //1.3 commonly used types
    //Unlike Java all types of variables are classes
    //in other words scala is purely object-oriented language
    val res0 = 1.toString
    println("res0: " + res0)
    println()

    //this is declarative style: tell you what I want to do
    //here, I want to get the number from 1 to 10
    //Range is one type of collection.
    //Int 1 is implicitly converted to RichInt, which has more functionality than Int class.
    val res1 = 1.to(10)

    //access each element of range and print it out
    res1.foreach(println)

    //calculate intersection between two strings
    val res2 = "hello".intersect("world")
    println("res2: " + res2)
    //output: res2: lo

    /* 1.4 Arithmetic and Operator Overloading
    * In general, (a method b) is shorthand for (a.method.(b))
    * for example, 1.to(10) is the same as 1 to 10 (infix notation) */
    val res3 = 1.to(10)

    //equivalent syntax
    val res4 = 1 to 10  //infix notation makes your code more readable

    //a + b is shorthand for a.+(b)
    //+ in scala is the method name
    //in other words scala has no silly prejudice against non-alphanumeric characters in method names

    val res5 = "Hello".intersect("world")
    //is the same as
    val res6 = "hello" intersect "world"

    var counter2 = 0
    counter2 += 1 //there are no increment (++) or decrement (--) operators, so you have to += and -=

    //BigInt is used to store very large integers
    //you can use the usual mathematical operators with BigInt objects
    val x: BigInt = 1234567890
    val res7 = x * x * x
    println("res7: " + res7)

    /* this is better than java because for bigints you have to use the multiply method which means
    * res7 would equal x.multiplty(x).multiply(x) which is very obtuse*/

    /* 1.5 calling functions and methods
    * In scala, the _ is the wildcard like * in java
    * we are importing all classes in the scala.math package */
    import scala.math._

    val res8 = sqrt(2)
    println("res8: " + res8)
    //output: res8: 1.4142135623

    val res9 = pow(2, 4)
    println("res9: " + res9)
    //output: res9: 16.0

    //Scala method without parameters often do not use parantheses
    //The method "distinct" outputs all of the unique characters in a string in order
    //left to right ignoring repeated letters
    val res10 = "hello".distinct
    println("res10: " + res10)
    //output: res10: helo

    //endline semicolons are optional in scala to save typing

    /* A Scala class has a companion object whose methods act just like static methods do in java.
    * The companion object to the BigInt class has a method called probablePrime */

    //produce random prime with 100 bit long
    val res11 = BigInt.probablePrime(100, scala.util.Random)
    println("res11: " + res11)

    //you can create a new bigint object without using the new operator
    val res12 = BigInt("1234567890")
    //is a shortcut for
    val res13 = BigInt.apply("1234567890")

    //why not requiring a new operator is good:
    val res14 = BigInt("1234567890") * BigInt("1123581321")
    println("res14: " + res14)

    /* using the apply method of a companion object is common scala idiom for constructing objects */

    //thanks to apply method of Array companion object
    val res15 = Array(1,4,9,16)
    //mkString will convert collection to a String
    println("res15: " + res15.mkString(", "))
    //output: res15: 1, 4, 9, 16
  }
}
