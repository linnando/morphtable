package org.linnando.morphtable

import scala.util.parsing.combinator._

class ConstraintParser extends RegexParsers {
  def parseExpression(string: String): ParseResult[Constraint] = parseAll(expression, string)

  def id: Parser[String] = "'" ~ """([^']|'')+""".r ~ "'" ^^ {
    case _ ~ id ~ _ => id.toString.replaceAll("''", "'")
  }

  def idList: Parser[List[String]] = "(" ~ id ~ rep("," ~ id) ~ ")" ^^ {
    case _ ~ id ~ ids ~ _ => id :: ids.map { case _ ~ id2 => id2 }
  }

  def equality: Parser[Constraint] = id ~ opt("!") ~ "=" ~ id ^^ {
    case element ~ None ~ _ ~ option => ConstraintInList(element, List(option))
    case element ~ Some(_) ~ _ ~ option => ConstraintNegation(ConstraintInList(element, List(option)))
  }

  def inList: Parser[Constraint] = id ~ opt("NOT") ~ "IN" ~ idList ^^ {
    case element ~ None ~ _ ~ options => ConstraintInList(element, options)
    case element ~ Some(_) ~ _ ~ options => ConstraintNegation(ConstraintInList(element, options))
  }

  def factor: Parser[Constraint] = equality | inList | opt("!") ~ "(" ~ expression ~ ")" ^^ {
    case None ~ _ ~ expression ~ _ => expression
    case Some(_) ~ _ ~ expression ~ _ => ConstraintNegation(expression)
  }

  def term: Parser[Constraint] = factor ~ rep("&&" ~ factor) ^^ {
    case f ~ Nil => f
    case f ~ fs => ConstraintConjunction(f :: fs.map { case _ ~ f2 => f2 })
  }

  def expression: Parser[Constraint] = term ~ rep("||" ~ term) ^^ {
    case t ~ Nil => t
    case t ~ ts => ConstraintDisjunction(t :: ts.map { case _ ~ t2 => t2 })
  }
}
