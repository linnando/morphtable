package org.linnando.morphtable

import angulate2.std._

import scala.scalajs.js

@Component(
  selector = "combinations",
  templateUrl = "src/main/resources/combinations.component.html",
  styleUrls = @@@("src/main/resources/combinations.component.css")
)
class CombinationsComponent(morphTableService: MorphTableService) extends OnInit {
  val parser = new ConstraintParser
  var constraintExpression: String = ""
  var errorMessage: String = ""
  var elementNames: js.Array[String] = js.Array()
  var elementOptions: js.Array[js.Array[String]] = js.Array()

  override def ngOnInit(): Unit = morphTableService.getConstraint map { c => constraintExpression = c }

  def generate(): Unit = {
    def allowedCombinations(elements: js.Array[Element], filter: Map[String, String] => Boolean): js.Array[js.Array[String]] = {
      val counters = elements.map(_ => 0)
      val optionNumbers = elements.map(_.options.length)

      def isAllowed: Boolean = {
        val options = elements.indices.map(j => (elements(j).name, elements(j).options(counters(j)))).toMap
        filter(options)
      }

      def getOptions: js.Array[String] = {
        val options = elements.indices.map(j => elements(j).options(counters(j)))
        js.Array[String]() ++ options
      }

      def advanceCounters(): Unit = {
        var j = counters.length - 1
        counters(j) += 1
        while (j >= 0 && counters(j) == optionNumbers(j)) {
          counters(j) = 0
          j -= 1
          if (j >= 0) counters(j) += 1
        }
      }

      val totalLength = optionNumbers.product
      val result = js.Array[js.Array[String]]()
      var i = 0
      while (i < totalLength) {
        if (isAllowed) result.push(getOptions)
        i += 1
        advanceCounters()
      }
      result
    }

    parser.parseExpression(constraintExpression) match {
      case parser.Success(matched, _) => matched match {
        case constraint: Constraint =>
          errorMessage = ""
          morphTableService.getElements map { es =>
            elementNames = es.map(_.name)
            elementOptions = allowedCombinations(es, constraint.apply)
          }
      }
      case parser.Failure(msg, _) => errorMessage = "FAILURE: " + msg
      case parser.Error(msg, _) => errorMessage = "ERROR: " + msg
    }
  }
}
