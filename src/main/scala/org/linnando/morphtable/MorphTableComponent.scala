package org.linnando.morphtable

import angulate2.std._

import scala.scalajs.js

@Component(
  selector = "morph-table",
  templateUrl = "src/main/resources/morph-table.component.html",
  styleUrls = @@@("src/main/resources/morph-table.component.css")
)
class MorphTableComponent(morphTableService: MorphTableService) extends OnInit {
  val Arr = js.Array
  var elements: js.Array[Element] = @@@()
  var maxOptionNumber = 0

  override def ngOnInit(): Unit = morphTableService.getElements map { es =>
    elements = es
    maxOptionNumber = es.map(e => e.options.length).max
  }

  def addElement(elementName: String): Unit = {
    val trimmed = elementName.trim
    if (trimmed.isEmpty) return
    morphTableService.addElement(trimmed) map { e =>
      elements.push(e)
      if (e.options.length > maxOptionNumber) maxOptionNumber = e.options.length
    }
  }

  def removeElement(elementIndex: Int): Unit = {
    morphTableService.removeElement(elementIndex) map { _ =>
      elements.splice(elementIndex, 1)
      maxOptionNumber = elements.map(e => e.options.length).max
    }
  }

  def addOption(elementIndex: Int, option: String): Unit = {
    val trimmed = option.trim
    if (trimmed.isEmpty) return
    morphTableService.addOption(elementIndex, trimmed) map { e =>
      elements.update(elementIndex, e)
      if (e.options.length > maxOptionNumber) maxOptionNumber = e.options.length
    }
  }

  def removeOption(elementIndex: Int, optionIndex: Int): Unit =
    morphTableService.removeOption(elementIndex, optionIndex) map { e =>
      elements.update(elementIndex, e)
      maxOptionNumber = elements.map(e => e.options.length).max
    }
}
