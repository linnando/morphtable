package org.linnando.morphtable

import org.specs2.mutable.Specification

class ConstraintParserSpec extends Specification {
  val parser = new ConstraintParser

  "id parser" should {
    "parse a single id" in {
      val result = parser.parseAll(parser.id, "'a'")
      result.successful must beTrue
      result.get must be equalTo "a"
    }

    "not parse an id without apostrophes" in {
      val result = parser.parseAll(parser.id, "a")
      result.successful must beFalse
    }

    "not parse an id with an apostrophe in the middle" in {
      val result = parser.parseAll(parser.id, "'a'b'")
      result.successful must beFalse
    }

    "parse an id with a double apostrophe in the middle" in {
      val result = parser.parseAll(parser.id, "'a''b'")
      result.successful must beTrue
      result.get must be equalTo "a'b"
    }
  }

  "id list parser" should {
    "not parse an empty list" in {
      val result = parser.parseAll(parser.idList, "()")
      result.successful must beFalse
    }

    "parse a list with a single id" in {
      val result = parser.parseAll(parser.idList, "('a')")
      result.successful must beTrue
      result.get must be equalTo List("a")
    }

    "parse a list with multiple ids" in {
      val result = parser.parseAll(parser.idList, "('a', 'b', 'c')")
      result.successful must beTrue
      result.get must be equalTo List("a", "b", "c")
    }

    "not parse a list without parentheses" in {
      val result = parser.parseAll(parser.idList, "'a', 'b', 'c'")
      result.successful must beFalse
    }
  }

  "equality parser" should {
    "parse an equality expression" in {
      val result = parser.parseAll(parser.equality, "'a' = 'b'")
      result.successful must beTrue
      result.get must be equalTo ConstraintInList("a", List("b"))
    }

    "parse an inequality expression" in {
      val result = parser.parseAll(parser.equality, "'a' != 'b'")
      result.successful must beTrue
      result.get must be equalTo ConstraintNegation(ConstraintInList("a", List("b")))
    }

    "not parse an expression without an operator" in {
      val result = parser.parseAll(parser.equality, "'a' 'b'")
      result.successful must beFalse
    }

    "not parse an expression with a disallowed operator" in {
      val result = parser.parseAll(parser.equality, "'a' === 'b'")
      result.successful must beFalse
    }
  }

  "inList parser" should {
    "parse an in-list expression" in {
      val result = parser.parseAll(parser.inList, "'a' IN ('b', 'c', 'd')")
      result.successful must beTrue
      result.get must be equalTo ConstraintInList("a", List("b", "c", "d"))
    }

    "parse a negation of an in-list expression" in {
      val result = parser.parseAll(parser.inList, "'a' NOT  IN ('b', 'c', 'd')")
      result.successful must beTrue
      result.get must be equalTo ConstraintNegation(ConstraintInList("a", List("b", "c", "d")))
    }

    "not parse an expression without IN" in {
      val result = parser.parseAll(parser.inList, "'a' ('b', 'c', 'd')")
      result.successful must beFalse
    }
  }

  "factor parser" should {
    "parse an equality expression" in {
      val result = parser.parseAll(parser.factor, "'a' = 'b'")
      result.successful must beTrue
      result.get must be equalTo ConstraintInList("a", List("b"))
    }

    "parse an in-list expression" in {
      val result = parser.parseAll(parser.factor, "'a' IN ('b', 'c', 'd')")
      result.successful must beTrue
      result.get must be equalTo ConstraintInList("a", List("b", "c", "d"))
    }

    "parse a complex expression in parentheses" in {
      val result = parser.parseAll(parser.factor, "('a' = 'b' || 'c' = 'd' && 'e' = 'f')")
      result.successful must beTrue
      result.get must be equalTo ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintConjunction(List(
          ConstraintInList("c", List("d")),
          ConstraintInList("e", List("f"))
        ))
      ))
    }

    "parse a negation of a complex expression in parentheses" in {
      val result = parser.parseAll(parser.factor, "!('a' = 'b' || 'c' = 'd' && 'e' = 'f')")
      result.successful must beTrue
      result.get must be equalTo ConstraintNegation(
        ConstraintDisjunction(List(
          ConstraintInList("a", List("b")),
          ConstraintConjunction(List(
            ConstraintInList("c", List("d")),
            ConstraintInList("e", List("f"))
          ))
        ))
      )
    }

    "not parse a complex expression without parentheses" in {
      val result = parser.parseAll(parser.factor, "'a' = 'b' || 'c' = 'd' && 'e' = 'f'")
      result.successful must beFalse
    }
  }

  "term parser" should {
    "parse a single factor" in {
      val result = parser.parseAll(parser.term, "'a' = 'b'")
      result.successful must beTrue
      result.get must be equalTo ConstraintInList("a", List("b"))
    }

    "parse a conjunction of factors" in {
      val result = parser.parseAll(parser.term, "'a' = 'b' && 'c' = 'd'")
      result.successful must beTrue
      result.get must be equalTo ConstraintConjunction(
        List(ConstraintInList("a", List("b")), ConstraintInList("c", List("d"))))
    }
  }

  "expression parser" should {
    "parse a single factor" in {
      val result = parser.parseAll(parser.expression, "'a' = 'b'")
      result.successful must beTrue
      result.get must be equalTo ConstraintInList("a", List("b"))
    }

    "parse a conjunction of factors" in {
      val result = parser.parseAll(parser.expression, "'a' = 'b' && 'c' = 'd'")
      result.successful must beTrue
      result.get must be equalTo ConstraintConjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintInList("c", List("d"))
      ))
    }

    "parse a disjunction of conjunctions of factors" in {
      val result = parser.parseAll(parser.expression, "'a' = 'b' || 'c' = 'd' && 'e' = 'f'")
      result.successful must beTrue
      result.get must be equalTo ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintConjunction(List(
          ConstraintInList("c", List("d")),
          ConstraintInList("e", List("f"))
        ))
      ))
    }
  }

  "parser" should {
    "parse a complex expression according to the priorities and parentheses" in {
      val result = parser.parseExpression("'a' = 'b' || 'c' = 'd' && ('e' = 'f' || 'g' = 'h')")
      result.successful must beTrue
      result.get must be equalTo ConstraintDisjunction(List(
        ConstraintInList("a", List("b")),
        ConstraintConjunction(List(
          ConstraintInList("c", List("d")),
          ConstraintDisjunction(List(
            ConstraintInList("e", List("f")),
            ConstraintInList("g", List("h"))
          ))
        ))
      ))
    }
  }
}
