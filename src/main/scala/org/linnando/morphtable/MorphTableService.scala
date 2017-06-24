package org.linnando.morphtable

import angulate2.std._
import rxjs.RxPromise

import scala.scalajs.js

@Injectable
class MorphTableService {
  val elements: js.Array[Element] = @@@(
    Element("Code editing", @@@("A")),
    Element("Displaying tool information", @@@("A")),
    Element("Language requirements check", @@@("A")),
    Element("Recommendations check", @@@("A")),
    Element("Compilation", @@@("Standalone", "Incremental")),
    Element("Software building", @@@("A")),
    Element("Software execution", @@@("A")),
    Element("Test execution", @@@("A")),
    Element("Software debugging", @@@("A")),
    Element("Version control", @@@("A"))
  )
  var constraint: String = "'Code editing' = 'A'"

  def getElements: RxPromise[js.Array[Element]] = RxPromise.resolve(elements.jsSlice())

  def addElement(elementName: String): RxPromise[Element] = {
    val element = Element(elementName, @@@())
    elements.push(element)
    RxPromise.resolve(element)
  }

  def removeElement(elementIndex: Int): RxPromise[Unit] = {
    elements.splice(elementIndex, 1)
    RxPromise.resolve(Unit)
  }

  def addOption(elementIndex: Int, option: String): RxPromise[Element] = {
    elements(elementIndex).options.push(option)
    RxPromise.resolve(elements(elementIndex))
  }

  def removeOption(elementIndex: Int, optionIndex: Int): RxPromise[Element] = {
    elements(elementIndex).options.splice(optionIndex, 1)
    RxPromise.resolve(elements(elementIndex))
  }

  def getConstraint: RxPromise[String] = RxPromise.resolve(constraint)
}
