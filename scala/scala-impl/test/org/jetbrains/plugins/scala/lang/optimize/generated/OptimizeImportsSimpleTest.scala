package org.jetbrains.plugins.scala.lang.optimize
package generated

class OptimizeImportsSimpleTest extends OptimizeImportsTestBase {
  //This class was generated by build script, please don't change this
  override def folderPath: String = super.folderPath + "simple/"


  protected override def rootPath(): String = folderPath

  def testFromRoot(): Unit = doTest()

  def testSorted(): Unit = doTest()

  def testSortedInPackage(): Unit = doTest()

  def testTwoExpressions(): Unit = doTest()

  def testDeleteBraces(): Unit = doTest()

  def testDontSaveNotResolved(): Unit = doTest()

  def testImportChainUsed(): Unit = doTest()

  def testLanguageFeatures(): Unit = doTest()

  def testNewLines(): Unit = doTest()

  def testOneImport(): Unit = doTest()

  def testScalaDoc(): Unit = doTest()

  def testSCL7275(): Unit = doTest()

  def testSomeTrait(): Unit = doTest()

  def testUnusedImportChain(): Unit = doTest()

  def testUnusedSelector(): Unit = doTest()

  def testUsedImport(): Unit = doTest()

  def testRelativeNameConflict(): Unit = doTest()

  def testNoReformattingComments(): Unit = doTest()
}