package org.linnando.morphtable

import org.specs2.mutable.Specification

class CombinationGenerationSpec extends Specification {
  "an in-list constraint" should {
    "restrict option list of the element" in {
      val constraint = ConstraintInList("a", List("b", "c"))
      val options = Map("a" -> List("b", "c", "d"))
      val combinations = constraint.generate(options)
      combinations must be equalTo Set(
        Map("a" -> "b"),
        Map("a" -> "c")
      )
    }

    "not restrict option list of other elements" in {
      val constraint = ConstraintInList("a", List("b", "c"))
      val options = Map("a" -> List("b", "c", "d"), "e" -> List("f", "g"))
      val combinations = constraint.generate(options)
      combinations must be equalTo Set(
        Map("a" -> "b", "e" -> "f"),
        Map("a" -> "c", "e" -> "f"),
        Map("a" -> "b", "e" -> "g"),
        Map("a" -> "c", "e" -> "g")
      )
    }
  }

  "a negation of an in-list constraint" should {
    "restrict option list of the element" in {
      val constraint = ConstraintNegation(ConstraintInList("a", List("b", "c")))
      val options = Map("a" -> List("b", "c", "d"))
      val combinations = constraint.generate(options)
      combinations must be equalTo Set(
        Map("a" -> "d")
      )
    }

    "not restrict option list of other elements" in {
      val constraint = ConstraintNegation(ConstraintInList("a", List("b", "c")))
      val options = Map("a" -> List("b", "c", "d"), "e" -> List("f", "g"))
      val combinations = constraint.generate(options)
      combinations must be equalTo Set(
        Map("a" -> "d", "e" -> "f"),
        Map("a" -> "d", "e" -> "g")
      )
    }
  }

  "a conjunction" should {
    "restrict option list of all included elements" in {
      val constraint = ConstraintConjunction(List(
        ConstraintInList("a", List("b", "c")),
        ConstraintInList("d", List("e"))
      ))
      val options = Map("a" -> List("c", "f"), "d" -> List("e", "g"))
      val combinations = constraint.generate(options)
      combinations must be equalTo Set(
        Map("a" -> "c", "d" -> "e")
      )
    }

    "not restrict option list of other elements" in {
      val constraint = ConstraintConjunction(List(
        ConstraintInList("a", List("b", "c")),
        ConstraintInList("d", List("e"))
      ))
      val options = Map("a" -> List("c", "f"), "d" -> List("e", "g"), "h" -> List("i", "j"))
      val combinations = constraint.generate(options)
      combinations must be equalTo Set(
        Map("a" -> "c", "d" -> "e", "h" -> "i"),
        Map("a" -> "c", "d" -> "e", "h" -> "j")
      )
    }
  }

  "a disjunction" should {
    "contain all allowed options" in {
      val constraint = ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintConjunction(List(
          ConstraintInList("a", List("c")),
          ConstraintInList("d", List("e"))
        ))
      ))
      val options = Map("a" -> List("b", "c", "f"), "d" -> List("e", "g", "h"))
      val combinations = constraint.generate(options)
      combinations must be equalTo Set(
        Map("a" -> "b", "d" -> "e"),
        Map("a" -> "b", "d" -> "g"),
        Map("a" -> "b", "d" -> "h"),
        Map("a" -> "c", "d" -> "e")
      )
    }
  }
}
