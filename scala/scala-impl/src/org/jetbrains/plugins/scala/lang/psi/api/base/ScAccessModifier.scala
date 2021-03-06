package org.jetbrains.plugins.scala
package lang
package psi
package api
package base

/**
* @author Alexander Podkhalyuzin
* Date: 07.03.2008
*/

trait ScAccessModifier extends ScalaPsiElement {

  def idText: Option[String]

  def isPrivate: Boolean
  def isProtected: Boolean
  def isThis: Boolean
  def isUnqualifiedPrivateOrThis: Boolean = isPrivate && (getReference == null || isThis)

  def modifierFormattedText: String = {
    val builder = new StringBuilder
    if (isPrivate) builder.append("private")
    else if (isProtected) builder.append("protected")
    else return ""
    if (isThis) {
      builder.append("[this]")
      return builder.toString()
    }
    idText match {
      case Some(id) => builder.append(s"[$id]")
      case _ =>
    }
    builder.toString()
  }
}