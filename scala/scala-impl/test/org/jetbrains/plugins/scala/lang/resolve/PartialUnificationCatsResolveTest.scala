package org.jetbrains.plugins.scala.lang.resolve

import org.jetbrains.plugins.scala.DependencyManagerBase._
import org.jetbrains.plugins.scala.base.ScalaLightCodeInsightFixtureTestAdapter
import org.jetbrains.plugins.scala.base.libraryLoaders.{IvyManagedLoader, LibraryLoader}
import org.jetbrains.plugins.scala.debugger.{ScalaVersion, Scala_2_12}

class PartialUnificationCatsResolveTest extends ScalaLightCodeInsightFixtureTestAdapter with SimpleResolveTestBase {
  import SimpleResolveTestBase._

  override implicit val version: ScalaVersion = Scala_2_12

  override def librariesLoaders: Seq[LibraryLoader] =
    super.librariesLoaders :+ IvyManagedLoader("org.typelevel" %% "cats-core" % "1.4.0")

  def testFunctionMap(): Unit = doResolveTest(
    s"""
       |import cats.instances.function._
       |import cats.syntax.functor._
       |
       |object fart extends App {
       |  val func1 = (a: Int) => a + 1
       |  val func2 = (a: Int) => a * 2
       |  val func3 = func1 m${REFSRC}ap func2
       |  println(func3(10))
       |}
    """.stripMargin, "FunctionMap.scala"
  )

  def testTupleMapN(): Unit = doResolveTest(
    s"""
       |case class Foo(s:String)
       |case class CountryCode(s: String)
       |object Foo {
       |  import cats.data.Validated
       |  import cats.syntax.validated._
       |
       |  private def hasIsoLengthCats(s: String): Validated[String,String] =
       |    if (s.length == 2) s.valid else "must have length 2".invalid
       |
       |  private def belongsToIsoOfficialCats(s: String): Validated[String,String] =
       |    if (s.startsWith("f")) s.valid else "must start with 'f'".invalid
       |
       |  def validated(s:String): Validated[String,CountryCode]={
       |    val x=(
       |      hasIsoLengthCats(s),
       |      belongsToIsoOfficialCats(s)
       |    )
       |
       |    import cats.syntax.apply._
       |    import cats.instances.string._
       |
       |    x.m${REFSRC}apN((s,_) => CountryCode(s))
       |  }
       |}
     """.stripMargin, "TupleMapN.scala"
  )

  def testIorFoldMap(): Unit = doResolveTest(
    s"""
       |import cats.implicits._
       |import cats.data.Ior
       |
       |val ior: Ior[Int, String] = ???
       |ior.fol${REFSRC}dMap(_.toString)
     """.stripMargin
  )
}
