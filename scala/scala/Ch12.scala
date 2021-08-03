/* This chapter will introduce major concepts of functional programming.
 *
 * In functional programming, functions are first-class citizens that are
 * passed around and manipulated just like any other data type.
 *
 * In Java before 1.8:
 * Data is the first class citizen and functions were second class citizens. */

object Ch12
{
  def main(args: Array[String]): Unit = {
    /* 12.1 Functions as Values
     *
     * In Scala, a function is a first class citizen just like a number.
     * You can store a function in a variable. */
    import scala.math._
    val num = 3.14

    // _ before the ceil function indicates that you really meant the functions and
    // you did not just forget to supply the arguments.
    // fun: Double => Double
    // fun is a variable containing a function, not a fixed function
    val fun = ceil _

    /* What can you do with a function? Call it. */
    val res0 = fun(num)
    println("res0: " + res0)
    //output: res0: 4.0

    /* In second way, you can give it to a function as an argument.
     *
     * The map function accepts a function, applies this function to each value in array,
     * and returns an array with the function value. It is similar to a Java for loop.
     *
     * Declarative style means "what" to do, not "how" to do.
     * I am going to map each element to a new element based on fun (declarative style) */
    val res1 = Array(3.14, 1.42, 2.0).map(fun)
    println("res1: " + res1.mkString(", "))
    //output: res1: 4.0, 2.0, 2.0

    /* 12.2 Anonymous Functions
     *
     * In Scala, you dont have to give a name to each function, just like you dont have
     * to give a name to each number. */
    val triple = (x: Double) => 3 * x
    // This is the same as: def triple(x: Double): Double = 3 * x
    val res3 = Array(3.14, 1.42, 2.0).map(triple)
    println("res3: " + res3.mkString(", "))
    //output: res1: 9.42, 4.26, 6.0

    /* But you dont have to name the function since this function may be used only once.
     * You can pass anonymous functions to another function.
     *
     * We tell the map method to multiply each element by 3. */
    val res4 = Array(3.14, 1.42, 2.0).map((x: Double) => 3 * x)

    //Infix notation
    val res5 = Array(3.14, 1.42, 2.0) map {(x: Double) => 3 * x}

    /* 12.3 Functions can take functions as parameters
     *
     * Evaluate any function f whose input is 0.25
     * f is variable holding a function
     *
     * type of this function
     * (paramType) => resultType
     * (Double => Double) => Double */
    def valueAtOneQuarter(f: Double => Double): Double = f(0.25)
    val res6 = valueAtOneQuarter(ceil _)
    println("res6: " + res6)
    //res6: 1.0

    val res7 = valueAtOneQuarter(sqrt _)
    println("res7: " + res7)
    //res7: 0.5

    /* Since valueAtOneQuarter is a function that receives a function,
     * it is called a higher order function. A higher order function can produce a function.
     * Functional programming is powerful because you can mathematically translate
     * formulas into code very easily.
     *
     * The power of mulBy is that it can deliver functions that multiply by any amount.
     *
     * What is type of this function: Double => (Double => Double) */
    def mulBy(factor: Double): Double => Double = {
      (x: Double) => factor * x   //Anonymous function
    }

    //Scala allows local functions
    def mulBy2(factor: Double): Double => Double = {
      def myfun(x: Double): Double = {
        factor * x
      }
      myfun
    }
    def fun4: Double => Double = {
      sqrt _
    }

    val quintuple = mulBy(5)
    val res8 = quintuple(20)
    println("res8: " + res8)
    //res8: 100

    //You can also do it in one step like this
    val res9 = mulBy(5)(20)

    /* 12.4 Parameter inference
     *
     * When you pass an anonymous function to another function, Scala helps you by
     * deducing types when possible.
     * def valueAtOneQuarter(f: Double => Double): Double = f(0.25) */
    val res10 = valueAtOneQuarter((x: Double) => x*3)

    //Since the function knows youll pass in Double => Double
    val res11 = valueAtOneQuarter((x) => x*3)

    //As a special bonus, for a function that has one parameter, you can omit the ()
    //around that parameter. If you have two or more, you cant omit them.
    val res12 = valueAtOneQuarter(x => x*3)

    /* If a parameter only occurs once on the right side of the =>
     * you can replace it with an _ */

    //a function that multiplies something by 3
    val res13 = valueAtOneQuarter(3 * _)

    //Keep in mind, these shortcuts only work when parameter type is known

    /* 12.5 Useful higher order functions
     *
     * Many methods in the Scala collection library uses higher order functions.
     * For the big data application, they also use lots of higher order functions.
     *
     * I want to map each value of this collection to 0.1 * value
     * This is a very quick way to produce a collection containing 0.1, 0.2, ... 0.9 */
    val res14 = (1 to 9). map(0.1 * _)
    println("res14: " + res14)
    //res14: Vector(0.1, 0.2, 0.30000000000000004, 0.4, 0.5, 0.6000000000000001, 0.7000000000000001, 0.8, 0.9)

    //The more concise and expensive, the better your code
    (1 to 9).map("*" * _).foreach(println)

    //Filter method
    //How to only get even numbers in a sequence
    val res15 = (1 to 9).filter(_ % 2 == 0)
    println("res15: " + res15)
    //res15: Vector(2, 4, 6, 8)

    //reductLeft
    //I want to produce something by combining elements from left to right
    val res16 = (1 to 5).reduceLeft((x, y) => x*y)
    println("res16: " + res16)
    //res16: 120

    val res17 = (1 to 5).reduceLeft(_ * _)

    val res18 = (1 to 5).reduceLeft((x: Int, y: Int) => x*y) //verbose version

    //split the string into array of string based on whitespace
    val res19 = "Mary had a little lamb".split(" ")
    println("res19: " + res19.mkString(","))
    //res19: Mary,had,a,little,lamb

    //sort the sequence of string based on length of each word
    val res20 = res19.sortWith((x, y) => x.length < y.length)
    println("res20: " + res20.mkString(","))
    //res20: a,had,Mary,lamb,little

    //sort the sequence of string based on value of first letter of each word
    val res20a = res19.sortWith((x, y) => x(0) < y(0))
    println("res20a: " + res20a.mkString(","))
    //res20a: Mary,a,had,little,lamb

    /* 12.6 Closure
     * Closure consists of code together with the definition of any nonlocal variables that code uses. */

    def mulBy3(factor: Double): Double => Double = {
      (y: Double) => factor * y
    }
    val triple2 = mulBy3(3)
    val half2 = mulBy3(0.5)
    /* Each of the returned functions has its own setting for factor.
     * Factor is a nonlocal variable, captured by the function. */

    // 12.8 Currying

    def mul(x: Int, y: Int): Int = x * y

    /* given x, I still need y to do this computation
     * this function takes one argument, yielding a function that takes another argument
     * you are doing a partial computation given x only
     * in many cases, x and y are not available at the same time. Perhaps, I only have x
     * I can get y from another program. In this situation, this function is very useful. */
    def mulOneAtATime(x: Int): Int => Int = (y: Int) => x * y

    //equivalent notation
    //you can put x and y into different parameter lists
    def mulOneAtATime2(x: Int)(y: Int): Int = x * y

    val x = 5
    //_ means I assign a function NOT function call to res21
    val res21 = mulOneAtATime2(x)_
    val y = 10
    val res22 = res21(y)  //when y is available, you can do final computation
    println("res22: " + res22)
    //res22: 50

    /* Why is currying useful?
     * Sometimes, only the value of x is available, you can call mulOneAtATime2 function to
     * produce partial computation. Then, when y is available. you can perform the complete
     * computation.
     * Furthermore, currying is important for type inference for compiler. */

    /* 12.9 Control Abstractions
     *
     * call-by-value parameter: parameter is always evaluated before function call
     * call-by-name parameter: (important concept in functional programming) parameter will
     * not be evaluated when you call the function. Parameter is evaluated on demand.
     *
     * this is called "lazy evaluation"
     * call-by-value is sometimes challenging since it is difficult to figure out the actual
     * memory usage of your program. However, lazy can be efficient.
     *
     * :=> means call by name
     * : means call by value */

    //block is call by name parameter
    //if the block is call by value. this piece of code will be run immediately before the
    //function is called
    //Here we want to execute this code in another thread.

    def runInThread(block: => Unit): Unit = {
      print("I am in function\n")
      //we will run the block in another thread
      //Another thread means this computation will happen in second thread
      new Thread {
        override def run(): Unit = {
          block
        }
      }.start()
    }

    runInThread{ print("Hi"); Thread.sleep(5000); println("Bye")}

    /* Lets define an until statement that works like while, but with an inverted condition.
     * Inverted condition means that if a condition is false, we continue; if its true, stop. */

    //we can to define condition as call by name because we want to evaluate this condition
    //several times. call by name is widely used for big data application
    def until(condition: => Boolean)(block: => Unit): Unit = {
      if(!condition) {
        block
        until(condition)(block)
      }
    }
    println()
    var z = 10

    //until is language keyword created by you
    //this is very powerful feature which makes scala extend its functionality very easily
    //if z == 0 is false i will continue loop
    until(z == 0) {
      z -= 1
      println(z)
    }
  }
}
