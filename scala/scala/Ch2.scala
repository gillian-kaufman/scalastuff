 /* In this chapter, we will discuss control structures and functions
  *
  * First let's discuss expressions vs. statements
  * An expression produces a value
  * A statement carries out an action, which usually has side effects (mutation)
  *
  * In Scala, almost all constructs have values, which is a major feature of functional programming.
  * These features can make programs more concise and easier to read
  */
object Ch2
{
  def main(args: Array[String]): Unit =
  {
    //2.1 Conditional expression
    //In Scala, if/else produce a value

    val x = 0

    //type of s is inferred, so you know its type during compiling time
    //scala is static typing like java so you dont need to write out the type of each variable
    //this makes your code more concise
    val s = if(x > 0) 1.0 else -1.0
    println("s: " + s)
    //output: s: -1.0

    /* type of a mixed expression is the common supertype of both branches
    * Any is equivalent to Object
    * common supertype of String and Int is Any */
    val res1 = if(x > 0) "positive" else -1

    //2.2 Statement
    var n = 10 //n is mutable
    var r = 1
    if (n > 0)
    {
      //This is called a statement. Statements usually have side effects
      //The side effect here is that you mutate r
      r = r * n
      n -= 1
    }

    //2.3 Block expression and assignment

    //In Scala, a block {} produces values. The value of the block is the value of the last expression.
    //The return expression is rarely used

    val x0 = 1.0
    val y0 = 1.0
    val x1 = 4.0
    val y1 = 5.0

    import scala.math._

    val distance =
    {
      //dx and dy are local variables
      val dx = x1 - x0
      val dy = y1 - y0
      //the last expression is the value of the block
      sqrt(dx * dx * dy * dy)
    }
    println("distance: " + distance)
    //output: distance: 5.0

    //2.5 Loops
    //r and n are mutable variables which were declared previously
    r = 1
    n = 10

    while (n > 0)
    {
      //This statement has side effects: you are mutating r
      r = r * n
      //n is counter, mutating n
      n -= 1
    }
    println("r: " + r + "n: " + n)
    //r: 3628800 n: 0

    //for any language features you need to evaluate its advantages and disadvantages
    //as a developer, you need to have a critical mindset

    //for expression
    //the construct for(i <- expr) makes i traverse all values of expr on the right

    r = 1
    n = 10
    //the type of local variable i is the element type of collection
    //value of i can be 1, 2...9, 10
    for (i <- 1 to n)
      r = r * i

    println("r: " + r)
    //r: 3628800

    val s1 = "hello"
    var sum1 = 0

    //until returns a range that does not include the upperbound
    for (i <- 0 until s1.length)
      sum1 += s1(i)

    println("sum1: " + sum1)
    //sum1: 500

    //for expression is a feature of functional programming. i is not mutated.
    //for every iteration i is recreated and reinitialized

    //In this example there is no need to use indexes as you can directly loop over character

    sum1 = 0
    for (ch <- "hello") sum1 += ch

    //2.6 Advanced for loops and for comprehensions
    //for comprehensions are an important feature of functional language

    //You can have multiple generators of the form variable <- expression
    //You can separate them by semicolons
    //There is no mutation in this loop. i and j are never mutated, just recreated at each iteration

    //inner loop (j <- 1 to 4) moves faster than outer loop (i <- 1 to 3)
    for (i <- 1 to 3; j <- 1 to 4) {
      print((10 * i + j) + " ")
    }
    //output: 10 11 12 13 14 21 22 23 24 31 32 33 34
    println()
    println()

    //each generator can have guard, a boolean condition preceded by if
    for (i <- 1 to 3; j <- 1 to 4 if i != j)
      print((10 * i + j) + " ")
    //output: 12 13 14 21 23 24 31 32 34
    println()
    println()

    /* You can have any number of definitions, introducing variable that can be used inside the loop
    * under the hood this code is translated to functional code using map and flatmap functions */

    for (i <- 1 to 3; from = 4 - i; j <- from to 3)
      print((10 * i + j) + " ")
    //output: 13 22 23 31 32 33
    println()
    println()

    //alternative syntax: enclose generators, guard, and definition inside {} (semicolon will be dropped)
    for
    {
      i <- 1 to 3
      from = 4 - 1
      j <- from to 3
    }
      print((10 * i + j) + " ")
    println()
    //output: 13 22 23 31 32 33

    /* when the body of the for loop starts with yield,
    * the loop constructs a collection of value, one for each iteration
    *
    * res18 is indexedsequence. Usually we use a vector to represent indexedsequence.
    * vectors are an immutable collection */

    val res18 = for (i <- 1 to 10) yield i % 3
    println("res18: " + res18)
    println()
    //res18: Vector(1, 2, 0, 1, 2, 0, 1, 2, 0, 1)

    //generated collection is compatible with the first generator
    val res19 = for (c <- "hello"; i <- 0 to 1) yield (c + i).toChar
    println("res19: " + res19)
    println()
    //output: res19: hieflmlmop

    val res20 = for (i <- 0 to 1; c <- "hello") yield (c + i).toChar
    println("res20: " + res20)
    println()
    //output: res20: Vector(h, e, l, l, o, i, f, m, m, p)

    //2.7 functions
    //public static double abs (equivalent java syntax)

    //Return is different in Scala than in Java. Return actually interrupts the thread
    def abs(x: Double): Double = if (x >= 0) x else -x

    println(abs(3))
    println(abs(-3))
    //output: 3.0

    //imperative style
    def fac(n: Int): Int = {
      var r = 1
      for (i <- 1 to n) r = r * i
      r   //return value for the block
    }
    println(fac(4))
    //output: 24

    /* for factorial, the functional style needs to use recursion
    * recursive functions do not have mutations. However, it is costly.
    * The beauty of recursive functions is that you get rid of the mutation inside the function. */

    def recursiveFac(n: Int): Int = {
      if (n <= 0) 1 else n * recursiveFac(n-1)
    }
    println(recursiveFac(4))
    //output: 24

    //2.8 default and named arguments
    //left and right both have default values
    def decorate(str: String, left: String = "[", right: String = "]"): String = {
      left + str + right
    }
    println(decorate(str = "Hello"))
    //output: [Hello]

    // right="]<<<" is called a named argument. Named arguments can make sure your code is
    // more readable if you have a large number of parameters
    println(decorate(str="Hello", right="]<<<"))
    //output: [Hello]<<<

    //2.9 variable arguments
    //Sometimes, it is convenient to implement a function that takes a variable number of arguments
    //args is an argument sequence, result is the local variable
    //if mutation only happens within the local function, that mutation will not affect
    //other parts of the program. This type of mutation is allowed for semi-functional style
    def sum(args: Int*): Int = {
      var result = 0
      for(arg <- args) result += arg
      result
    }
    val result1 = sum(args = 1, 4, 9, 16, 25)
    println("result1: " + result1)
    //output: result1: 55

    val result2 = sum(args = 1, 4)
    println("result2: " + result2)
    //output: result2: 5

    val result3 = sum()
    println("result3: " + result3)
    //output: result1: 0

    // an _ tells the compiler that you want the parameter to be considered as an argument sequence
    val result4 = sum(1 to 5: _*)
    println("result4: " + result4)
    //output: result4: 15

    //2.10 procedure
    //a procedure is a function with nothing to return
    //: Unit means that this function does not return anything
    //box will print the border for the String
    def box(s: String): Unit = {
      val border = "-" * s.length + "--\n"
      println("\n" + border + "|" + s + "|\n" + border)
    }
    box("Fred")
    box("USC Upstate")

    //2.12 Exception
    //Exceptions happen when you have some illegal operations
    //Exception expression has a special data type called Nothing
    def root(x: Double): Double = {
      if (x >= 0)
        sqrt(x)
      else
        throw new IllegalArgumentException("x should not be negative")
    }

    try
    {
      println(4)
      println(-4)
    }
    catch
    {
      case e: Exception => println(e)
    }

    //println(root(-4))
    //output: 2.0
    //java.lang.IllegalArgumentException: x should not be negative
  }
}
