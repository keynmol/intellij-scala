package org.jetbrains.plugins.scala.structureView

import javax.swing.Icon

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.util.PlatformIcons
import com.intellij.util.PlatformIcons._
import org.intellij.lang.annotations.Language
import org.jetbrains.plugins.scala.ScalaFileType
import org.jetbrains.plugins.scala.base.ScalaLightCodeInsightFixtureTestAdapter
import org.jetbrains.plugins.scala.icons.Icons._
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.jetbrains.plugins.scala.lang.structureView.ScalaStructureViewModel
import org.jetbrains.plugins.scala.structureView.ScalaStructureViewTest._
import org.junit.Assert

import scala.util.matching.Regex

/**
  * @author Pavel Fatin
  */
class ScalaStructureViewTest extends ScalaLightCodeInsightFixtureTestAdapter {
  def testEmptyFile(): Unit = {
    check("")
  }

  def testVariable(): Unit = {
    check("""
          var v: Int = 1
          """,
      Node(VAR, "v: Int"))
  }

  def testMultipleVariables(): Unit = {
    check("""
          var v1, v2: Int = 1
          """,
      Node(VAR, "v1: Int"),
      Node(VAR, "v2: Int"))
  }

  def testMemberVariable(): Unit = {
    check("""
          class Container {
            var v: Int = 1
          }
          """,
      Node(CLASS, "Container",
        Node(FIELD_VAR, "v: Int")))
  }

  def testAbstractMemberVariable(): Unit = {
    check("""
          trait Container {
            var v: Int
          }
          """,
      Node(TRAIT, "Container",
        Node(ABSTRACT_FIELD_VAR, "v: Int")))
  }

  def testVariableTypeInference(): Unit = {
    check("""
          var v = 1
          """,
      Node(VAR, "v: Int"))
  }

  def testValue(): Unit = {
    check("""
          val v: Int = 1
          """,
      Node(VAL, "v: Int"))
  }

  def testMultipleValues(): Unit = {
    check("""
          val v1, v2: Int = 1
          """,
      Node(VAL, "v1: Int"),
      Node(VAL, "v2: Int"))
  }

  def testValueTypeInference(): Unit = {
    check("""
          val v = 1
          """,
      Node(VAL, "v: Int"))
  }

  def testTypeAlias(): Unit = {
    check("""
          type A = Int
          """,
      Node(TYPE_ALIAS, "A"))
  }

  def testMethod(): Unit = {
    check("""
          def m: Int = 1
          """,
      Node(FUNCTION, "m: Int"))
  }

  def testMethodTypeInference(): Unit = {
    check("""
          def m = 1
          """,
      Node(FUNCTION, "m: Int"))
  }

  def testTypeParametersInMethod(): Unit = {
    check("""
          def m[A, B]: Int = 1
          """,
      Node(FUNCTION, "m[A, B]: Int"))
  }

//  def testMethodTypeParameterPresentation(): Unit = {
//    check("""
//          def m[T <: Any]: Int = 1
//          """,
//      Node(FUNCTION, "m[T]: Int"))
//  }

  def testParameterListInMethod(): Unit = {
    check("""
          def m(): Int = 1
          """,
      Node(FUNCTION, "m(): Int"))
  }

  def testMultipleParametersInMethod(): Unit = {
    check("""
          def m(p1: Float, p2: Double): Int = 1
          """,
      Node(FUNCTION, "m(Float, Double): Int"))
  }

  def testMultipleParameterListsInMethod(): Unit = {
    check("""
          def m(p1: Float)(p2: Double): Int = 1
          """,
      Node(FUNCTION, "m(Float)(Double): Int"))
  }

  def testObject(): Unit = {
    check("""
          object O
          """,
      Node(OBJECT, "O"))
  }

  def testClass(): Unit = {
    check("""
          class C
          """,
      Node(CLASS, "C"))
  }

  def testClassTypeParameters(): Unit = {
    check("""
          class C[A, B]
          """,
      Node(CLASS, "C[A, B]"))
  }

  def testTrait(): Unit = {
    check("""
          trait T
          """,
      Node(TRAIT, "T"))
  }

  def testTraitTypeParameters(): Unit = {
    check("""
          trait T[A, B]
          """,
      Node(TRAIT, "T[A, B]"))
  }

  def testTypeDefinitionTypeParameterPresentation(): Unit = {
    check("""
          class C[T <: Any]
          """,
      Node(CLASS, "C[T]"))
  }

  def testParameterListInPrimaryConstructor(): Unit = {
    check("""
          class C()
          """,
      Node(CLASS, "C()"))
  }

  def testMultipleParametersInPrimaryConstructor(): Unit = {
    check("""
          class C(p1: Float, p2: Double)
          """,
      Node(CLASS, "C(Float, Double)"))
  }

  def testMultipleParameterListsInPrimaryConstructor(): Unit = {
    check("""
          class C(p1: Float)(p2: Double)
          """,
      Node(CLASS, "C(Float)(Double)"))
  }

//  def testVariablesInPrimaryConstructor(): Unit = {
//    check("""
//          class C(var p1: Float, p2: Double)
//          """,
//      Node(CLASS, "C(Float, Double)",
//        Node(VAR, "p1"),
//        Node(VAR, "p2")))
//  }

//  def testValuesInPrimaryConstructor(): Unit = {
//    check("""
//          class C(val p1: Floar, val p2: Double)
//          """,
//      Node(CLASS, "C(Float, Double)",
//        Node(FIELD_VAL, "p1",
//        Node(FIELD_VAL, "p2")))
//  }

//  def testMultipleParameterListsWithMembersInPrimaryConstructor(): Unit = {
//    check("""
//          class C(val p1: Float)(val p2: Double)
//          """,
//      Node(CLASS, "C(Float)(Double)",
//        Node(FIELD_VAL, "p1",
//        Node(FIELD_VAL, "p2"))))
//  }

  def testAuxiliaryConstructor(): Unit = {
    check("""
          class C {
            def this() { this() }
          }
          """,
      Node(CLASS, "C",
        Node(METHOD_ICON, "this()")))
  }

  def testParameterInAuxiliaryConstructor(): Unit = {
    check("""
          class C {
            def this(p: Int) { this() }
          }
          """,
      Node(CLASS, "C",
        Node(METHOD_ICON, "this(Int)")))
  }

  def testMultipleParametersInAuxiliaryConstructor(): Unit = {
    check("""
          class C {
            def this(p1: Float, p2: Double) { this() }
          }
          """,
      Node(CLASS, "C",
        Node(METHOD_ICON, "this(Float, Double)")))
  }

  def testMultipleParameterListsInAuxiliaryConstructor(): Unit = {
    check("""
          class C {
            def this(p1: Float)(p2: Double) { this() }
          }
          """,
      Node(CLASS, "C",
        Node(METHOD_ICON, "this(Float)(Double)")))
  }

  def testBlock(): Unit = {
    check("""
          {}
          """,
      Node(CLASS_INITIALIZER, ""))
  }

  def testInsideClass(): Unit = {
    check("""
          class Container {
            {}
            var v1: Int = 1
            val v2: Int = 1
            type A = Int
            def m: Int = 1
            class C
            trait T
            object O
          }
          """,
      Node(CLASS, "Container",
        Node(CLASS_INITIALIZER, ""),
        Node(FIELD_VAR, "v1: Int"),
        Node(FIELD_VAL, "v2: Int"),
        Node(TYPE_ALIAS, "A"),
        Node(METHOD_ICON, "m: Int"),
        Node(CLASS, "C"),
        Node(TRAIT, "T"),
        Node(OBJECT, "O")))
  }

  def testInsideTrait(): Unit = {
    check("""
          trait Container {
            {}
            var v1: Int = 1
            val v2: Int = 1
            type A = Int
            def m: Int = 1
            class C
            trait T
            object O
          }
          """,
      Node(TRAIT, "Container",
        Node(CLASS_INITIALIZER, ""),
        Node(FIELD_VAR, "v1: Int"),
        Node(FIELD_VAL, "v2: Int"),
        Node(TYPE_ALIAS, "A"),
        Node(METHOD_ICON, "m: Int"),
        Node(CLASS, "C"),
        Node(TRAIT, "T"),
        Node(OBJECT, "O")))
  }

  def testInsideObject(): Unit = {
    check("""
          object Container {
            {}
            var v1: Int = 1
            val v2: Int = 1
            type A = Int
            def m: Int = 1
            class C
            trait T
            object O
          }
          """,
      Node(OBJECT, "Container",
        Node(CLASS_INITIALIZER, ""),
        Node(FIELD_VAR, "v1: Int"),
        Node(FIELD_VAL, "v2: Int"),
        Node(TYPE_ALIAS, "A"),
        Node(METHOD_ICON, "m: Int"),
        Node(CLASS, "C"),
        Node(TRAIT, "T"),
        Node(OBJECT, "O")))
  }

  def testInsideBlock(): Unit = {
    check("""
          {
            {}
            var v1: Int = 1
            val v2: Int = 1
            type A = Int
            def m: Int = 1
            class C
            trait T
            object O
          }
          """,
      Node(CLASS_INITIALIZER, "",
        Node(CLASS_INITIALIZER, ""),
        Node(FUNCTION, "m: Int"),
        Node(CLASS, "C"),
        Node(TRAIT, "T"),
        Node(OBJECT, "O")))
  }

  def testInsideVariable(): Unit = {
    check("""
          var v: Int = {
            {}
            var v1: Int = 1
            val v2: Int = 1
            type A = Int
            def m: Int = 1
            class C
            trait T
            object O
          }
          """,
      Node(VAR, "v: Int",
        Node(CLASS_INITIALIZER, ""),
        Node(FUNCTION, "m: Int"),
        Node(CLASS, "C"),
        Node(TRAIT, "T"),
        Node(OBJECT, "O")))
  }

  def testInsideValue(): Unit = {
    check("""
          val v: Int = {
            {}
            var v1: Int = 1
            val v2: Int = 1
            type A = Int
            def m: Int = 1
            class C
            trait T
            object O
          }
          """,
      Node(VAL, "v: Int",
        Node(CLASS_INITIALIZER, ""),
        Node(FUNCTION, "m: Int"),
        Node(CLASS, "C"),
        Node(TRAIT, "T"),
        Node(OBJECT, "O")))
  }

  def testInsideMethod(): Unit = {
    check("""
          def m: Int = {
            {}
            var v1: Int = 1
            val v2: Int = 1
            type A = Int
            def m: Int = 1
            class C
            trait T
            object O
          }
          """,
      Node(FUNCTION, "m: Int",
        Node(CLASS_INITIALIZER, ""),
        Node(FUNCTION, "m: Int"),
        Node(CLASS, "C"),
        Node(TRAIT, "T"),
        Node(OBJECT, "O")))
  }

//  def testMultipleClasses(): Unit = {
//    check("""
//          class A
//          class B
//          """,
//      Node(CLASS, "A",
//        Node(FUNCTION, "this")),
//      Node(CLASS, "B",
//        Node(FUNCTION, "this")))
//  }
//
//  def testClassAndObject(): Unit = {
//    check("""
//          class C
//          object C
//          """,
//      Node(CLASS, "C",
//        Node(FUNCTION, "this")),
//      Node(OBJECT, "C"))
//  }

  private def check(@Language("Scala") code: String, nodes: Node*): Unit = {
    val actualNode = {
      val file = psiFileOf(code)(getProject)
      val model = new ScalaStructureViewModel(file)
      Node(model.getRoot)
    }

    val expectedNode = Node(FILE, "foo.scala", nodes: _*)

    Assert.assertEquals(expectedNode.toString, actualNode.toString)
  }
}

private object ScalaStructureViewTest {
  private final val IconFileName = new Regex("(?<=/)[^/]+(?=\\.png)")

  case class Node(icon: Icon, name: String, children: Node*) {
    override def toString: String =
      IconFileName.findFirstIn(Option(icon).mkString).mkString("[", "", "] ") + name + "\n" +
        children.map(node => "  " + node.toString).mkString
  }

  object Node {
    def apply(element: StructureViewTreeElement): Node = {
      val presentation = element.getPresentation

      Node(presentation.getIcon(true), presentation.getPresentableText,
        element.getChildren.map { case element: StructureViewTreeElement => Node(element) }: _*)
    }
  }

  def psiFileOf(@Language("Scala") s: String)(project: Project): ScalaFile = {
    PsiFileFactory.getInstance(project)
      .createFileFromText("foo.scala", ScalaFileType.INSTANCE, s)
      .asInstanceOf[ScalaFile]
  }
}