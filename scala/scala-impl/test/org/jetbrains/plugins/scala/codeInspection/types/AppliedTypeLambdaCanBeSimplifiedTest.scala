package org.jetbrains.plugins.scala.codeInspection.types

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.testFramework.EditorTestUtil
import org.jetbrains.plugins.scala.codeInspection.typeLambdaSimplify.AppliedTypeLambdaCanBeSimplifiedInspection
import org.jetbrains.plugins.scala.codeInspection.{InspectionBundle, ScalaQuickFixTestBase}
import org.jetbrains.plugins.scala.project.settings.ScalaCompilerConfiguration

/**
 * Author: Svyatoslav Ilinskiy
 * Date: 7/6/15
 */
class AppliedTypeLambdaCanBeSimplifiedTest extends ScalaQuickFixTestBase {

  import EditorTestUtil.{SELECTION_END_TAG => END, SELECTION_START_TAG => START}

  override protected val classOfInspection: Class[_ <: LocalInspectionTool] = classOf[AppliedTypeLambdaCanBeSimplifiedInspection]

  override protected val description: String = InspectionBundle.message("applied.type.lambda.can.be.simplified")

  private val hint: String = InspectionBundle.message("simplify.type")

  private def testFix(text: String, res: String): Unit = testQuickFix(text, res, hint)

  override protected def setUp(): Unit = {
    super.setUp()

    val defaultProfile = ScalaCompilerConfiguration.instanceIn(getProject).defaultProfile
    val newSettings = defaultProfile.getSettings
    newSettings.plugins :+= "kind-projector" //only some of the tests require kind-projector
    defaultProfile.setSettings(newSettings)
  }

  def testSimple(): Unit = {
    val text = s"def a: $START({type l[a] = Either[String, a]})#l[Int]$END)"
    checkTextHasError(text)
    val code = "def a: ({type l[a] = Either[String, a]})#l[Int]"
    val res = "def a: Either[String, Int]"
    testFix(code, res)
  }

  def testKindProjectorFunctionSyntax(): Unit = {
    val text = s"def a: ${START}Lambda[A => (A, A)][Int]$END"
    checkTextHasError(text)
    val code = "def a: Lambda[A => (A, A)][Int]"
    val res = "def a: (Int, Int)"
    testFix(code, res)
  }

  def testKindProjectorInlineSyntax(): Unit = {
    val text = s"def a: ${START}Either[+?, -?][String, Int]$END"
    checkTextHasError(text)
    val code = "def a: Either[+?, -?][String, Int]"
    val res = "def a: Either[String, Int]"
    testFix(code, res)
  }
}
