package org.linnando.morphtable

import org.specs2.mutable.Specification

class ConstraintNormalizationSpec extends Specification {
  "normalizer" should {
    "preserve an in-list expression" in {
      val constraint = ConstraintInList("a", List("b"))
      constraint.normalForm must be equalTo constraint
    }

    "preserve a negation of an in-list expression" in {
      val constraint = ConstraintNegation(ConstraintInList("a", List("b")))
      constraint.normalForm must be equalTo constraint
    }

    "eliminate double negation" in {
      val constraint = ConstraintNegation(ConstraintNegation(ConstraintInList("a", List("b"))))
      constraint.normalForm must be equalTo ConstraintInList("a", List("b"))
    }

    "preserve a simple disjunction" in {
      val constraint = ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintInList("c", List("d"))
      ))
      constraint.normalForm must be equalTo constraint
    }

    "normalise a negation of a disjunction" in {
      val constraint = ConstraintNegation(
        ConstraintDisjunction(List(
          ConstraintInList("a", List("b")),
          ConstraintInList("c", List("d"))
        ))
      )
      constraint.normalForm must be equalTo ConstraintConjunction(List(
        ConstraintNegation(ConstraintInList("a", List("b"))),
        ConstraintNegation(ConstraintInList("c", List("d")))
      ))
    }

    "preserve a simple conjunction" in {
      val constraint = ConstraintConjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintInList("c", List("d"))
      ))
      constraint.normalForm must be equalTo constraint
    }

    "normalise a negation of a conjunction" in {
      val constraint = ConstraintNegation(
        ConstraintConjunction(List(
          ConstraintInList("a", List("b")),
          ConstraintInList("c", List("d"))
        ))
      )
      constraint.normalForm must be equalTo ConstraintDisjunction(List(
        ConstraintNegation(ConstraintInList("a", List("b"))),
        ConstraintNegation(ConstraintInList("c", List("d")))
      ))
    }

    "preserve a DNF" in {
      val constraint = ConstraintDisjunction(List(
        ConstraintConjunction(List(
          ConstraintInList("a", List("b")),
          ConstraintInList("c", List("d"))
        )),
        ConstraintConjunction(List(
          ConstraintInList("e", List("f")),
          ConstraintInList("g", List("h"))
        ))
      ))
      constraint.normalForm must be equalTo constraint
    }

    "invert a CNF" in {
      val constraint = ConstraintConjunction(List(
        ConstraintDisjunction(List(
          ConstraintInList("a", List("b")),
          ConstraintInList("c", List("d"))
        )),
        ConstraintDisjunction(List(
          ConstraintInList("e", List("f")),
          ConstraintInList("g", List("h"))
        ))
      ))
      constraint.normalForm must be equalTo ConstraintDisjunction(List(
        ConstraintConjunction(List(
          ConstraintInList("a", List("b")),
          ConstraintInList("e", List("f"))
        )),
        ConstraintConjunction(List(
          ConstraintInList("a", List("b")),
          ConstraintInList("g", List("h"))
        )),
        ConstraintConjunction(List(
          ConstraintInList("c", List("d")),
          ConstraintInList("e", List("f"))
        )),
        ConstraintConjunction(List(
          ConstraintInList("c", List("d")),
          ConstraintInList("g", List("h"))
        ))
      ))
    }

    "normalize a complex expression" in {
      val constraint = ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintConjunction(List(
          ConstraintInList("c", List("d")),
          ConstraintDisjunction(List(
            ConstraintInList("e", List("f")),
            ConstraintInList("g", List("h"))
          ))
        ))
      ))
      constraint.normalForm must be equalTo ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintConjunction(List(
          ConstraintInList("c", List("d")),
          ConstraintInList("e", List("f"))
        )),
        ConstraintConjunction(List(
          ConstraintInList("c", List("d")),
          ConstraintInList("g", List("h"))
        ))
      ))
    }

    "normalize a complex expression with negation" in {
      val constraint = ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintConjunction(List(
          ConstraintNegation(
            ConstraintConjunction(List(
              ConstraintInList("c", List("d")),
              ConstraintInList("e", List("f"))
            ))
          ),
          ConstraintDisjunction(List(
            ConstraintInList("e", List("f")),
            ConstraintInList("g", List("h"))
          ))
        ))
      ))
      constraint.normalForm must be equalTo ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintConjunction(List(
          ConstraintNegation(ConstraintInList("c", List("d"))),
          ConstraintInList("e", List("f"))
        )),
        ConstraintConjunction(List(
          ConstraintNegation(ConstraintInList("c", List("d"))),
          ConstraintInList("g", List("h"))
        )),
        ConstraintConjunction(List(
          ConstraintNegation(ConstraintInList("e", List("f"))),
          ConstraintInList("e", List("f"))
        )),
        ConstraintConjunction(List(
          ConstraintNegation(ConstraintInList("e", List("f"))),
          ConstraintInList("g", List("h"))
        ))
      ))
    }
  }
}
