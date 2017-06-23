package org.linnando.morphtable

import angulate2.std._
import rxjs.RxPromise

import scala.scalajs.js

@Injectable
class MorphTableService {
  val elements: js.Array[Element] = @@@(
    Element("Code editing", @@@()),
    Element("Displaying tool information", @@@()),
    Element("Language requirements check", @@@()),
    Element("Recommendations check", @@@()),
    Element("Compilation", @@@("Standalone", "Incremental")),
    Element("Software building", @@@()),
    Element("Software execution", @@@()),
    Element("Test execution", @@@()),
    Element("Software debugging", @@@()),
    Element("Version control", @@@())
  )

  def getElements: RxPromise[js.Array[Element]] = RxPromise.resolve(elements.jsSlice())

  def addElement(elementName: String): RxPromise[Element] = {
    val element = Element(elementName, @@@())
    elements.push(element)
    RxPromise.resolve(element)
  }

  def removeElement(elementIndex: Int): RxPromise[Unit] = {
    elements.splice(elementIndex, 1)
    RxPromise.resolve()
  }

  def addOption(elementIndex: Int, option: String): RxPromise[Element] = {
    elements(elementIndex).options.push(option)
    RxPromise.resolve(elements(elementIndex))
  }

  def removeOption(elementIndex: Int, optionIndex: Int): RxPromise[Element] = {
    elements(elementIndex).options.splice(optionIndex, 1)
    RxPromise.resolve(elements(elementIndex))
  }
}
