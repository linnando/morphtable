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
    val parsed = parser.parseExpression(constraintExpression)
    parsed match {
      case parser.Success(matched, _) => matched match {
        case constraint: Constraint => {
          errorMessage = ""
          morphTableService.getElements map { es =>
            val elements = es.map(e => (e.name, e.options.toList)).toMap
            elementNames = es.map(_.name)
            val combinations = js.Array[Map[String, String]]() ++ constraint.generate(elements)
            elementOptions = combinations.map(options => elementNames.map(name => options(name)))
          }
        }
      }
      case parser.Failure(msg, _) => errorMessage = "FAILURE: " + msg
      case parser.Error(msg, _) => errorMessage = "ERROR: " + msg
    }
  }
}
