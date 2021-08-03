/* In this section, we will learn when to use the object construct in Scala.
 * Object construct is used when you need a class with a single instance, or
 * when you want to find a home for various values or functions, which is
 * very similar to the static method in Java.
 *
 * 6.1 Singletons
 * Scala has no static methods or static fields. Instead, you use the object
 * construct. An object defines a single instance of a class with features
 * that you want.
 *
 * In our example, the Accounts constructor is executed when the first call to
 * Accounts.newUniqueNumber(). If an object is never used, its constructors are
 * not executed at all. */
object Accounts
{
  //similar to the static variable field in Java
  private var lastNumber = 0

  def newUniqueNumber(): Int = {
    lastNumber += 1
    lastNumber
  }
} //end of Accounts object
/* 6.2 Companion Objects
 * In Java or C++, you often have a class with both instance methods and static methods.
 * In Scala, you achieve this by having a class and companion object of the same name.
 * In other words, the companion object stores static members and static methods like Java.
 *
 * The primary constructor is private. Nobody outside can call this constructor.
 * val means field. Without val, it is a class parameter. */

// you have two fields; the first (id) is immutable, and the second (balance) is mutable
class Accounts1 private(val id: Int, initialBalance: Double)
{
  // declare second field
  private var balance = initialBalance

  def deposit(amount: Double): Unit = {balance += amount}

  def description: String = "Account " + id + " with balance " + balance
}
// Companion Object
// This will contain static method and variable for Account1 class
object Accounts1
{
  // this is the field for object Account1, which is similar to the static member of Java
  private var lastNumber = 0

  private def newUniqueNumber(): Int = {
    lastNumber += 1; lastNumber
  }
  /* It is common to have an object with an apply method. Typically, such apply methods return
   * an object of the companion class.
   * The Accounts1 object can only have one instance.
   * The object of the companion class Accounts1 can have multiple instances.
   *
   * Since the primary constructor of a companion class is private, the only way to create
   * these objects is by calling the apply method. */

  def apply(initialBalance: Double): Accounts1 = {
    new Accounts1(newUniqueNumber(), initialBalance)
  }
} //end of Accounts1 object

/* 6.3 Objects extending a class or Trait (interface)
 *
 * A Trait is very similar to interface in Java, but more powerful.
 * One useful application is to specify default objects that can be shared.
 *
 * The abstract class means that one or several method's implementation is empty.
 * This is useful for inheritance. */

abstract class UndoableAction(val description: String)
{
  def undo(): Unit
  def redo(): Unit
}

/* A useful default is the "do nothing" action. Of course, we only need one of them.
 * Extends means that DoNothingAction is the subclass of UndoableAction. */
object DoNothingAction extends UndoableAction("Do Nothing")
{
  def undo() {} // we define empty action by leaving {} blank
  def redo() {}
}
// The DoNothingAction object can be shared across all places that need this default

object Ch6
{
  def main(args: Array[String]): Unit = {
    // when newUniqueNumber is called for the first time, the Accounts object is created
    // (which is a single instance)
    val res0 = Accounts.newUniqueNumber()
    println("res0: " + res0)
    // output: res0: 1

    // refer to the same Accounts object
    val res1 = Accounts.newUniqueNumber()
    // every time you call newUniqueNumber, the value of its counter increases

    // You cannot have a second Accounts object in your program
    val res1a = Accounts.newUniqueNumber()

    // This is equivalent to calling Accounts1.apply(1000.0)
    val acct1 = Accounts1(1000.0)
    println(acct1.description)
    // output: Account 1 with balance 1000.0
    val acct2 = Accounts1(2300.0)
    println(acct2.description)
    // output: Account 2 with balance 2300.0

    val actions = Map("open" -> DoNothingAction, "save" -> DoNothingAction)
  }
}
