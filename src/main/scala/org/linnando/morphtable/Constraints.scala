package org.linnando.morphtable

abstract sealed class Constraint {
  def apply(elementValues: Map[String, String]): Boolean

  def normalForm: Constraint

  def generate(elements: Map[String, List[String]]): Set[Map[String, String]]
}

case class ConstraintInList(element: String, options: List[String]) extends Constraint {
  override def apply(elementValues: Map[String, String]): Boolean = options.contains(elementValues(element))

  override def normalForm: Constraint = this

  override def generate(elements: Map[String, List[String]]): Set[Map[String, String]] =
    ConstraintConjunction(List(this)).generate(elements)

  def restrictedValues(elements: Map[String, Set[String]]): Map[String, Set[String]] =
    elements.updated(element, elements(element).intersect(options.toSet))

  def excludedValues(elements: Map[String, Set[String]]): Map[String, Set[String]] =
    elements.updated(element, elements(element) -- options.toSet)
}

case class ConstraintNegation(proposition: Constraint) extends Constraint {
  override def apply(elementValues: Map[String, String]): Boolean = !proposition(elementValues)

  override def normalForm: Constraint = proposition match {
    case _: ConstraintInList => this
    case ConstraintNegation(x) => x.normalForm
    case ConstraintDisjunction(disjuncts) =>
      ConstraintConjunction(disjuncts.map(ConstraintNegation)).normalForm
    case ConstraintConjunction(conjuncts) =>
      ConstraintDisjunction(conjuncts.map(ConstraintNegation)).normalForm
  }

  override def generate(elements: Map[String, List[String]]): Set[Map[String, String]] =
    ConstraintConjunction(List(this)).generate(elements)

  def restrictedValues(elements: Map[String, Set[String]]): Map[String, Set[String]] = proposition match {
    case x: ConstraintInList => x.excludedValues(elements)
    case _ => throw new NotADNFException
  }

}

case class ConstraintConjunction(conjuncts: List[Constraint]) extends Constraint {
  override def apply(elementValues: Map[String, String]): Boolean = conjuncts.forall(_(elementValues))

  override def normalForm: Constraint = {
    def cartesian(disjuncts: List[List[Constraint]], acc: List[List[Constraint]]): List[List[Constraint]] = disjuncts match {
      case Nil => acc
      case head :: tail =>
        val next = for {
          d <- head
          ds <- acc
        } yield d :: ds
        cartesian(tail, next)
    }

    val normalizedDisjuncts = conjuncts.map(_.normalForm match {
      case ConstraintDisjunction(literalConjunctions) => literalConjunctions
      case x => List(x)
    })
    val distributedDisjuncts = cartesian(normalizedDisjuncts.reverse, List(Nil)).map(ConstraintConjunction)
    if (distributedDisjuncts.length == 1) distributedDisjuncts.head
    else ConstraintDisjunction(distributedDisjuncts)
  }

  override def generate(elements: Map[String, List[String]]): Set[Map[String, String]] = {
    def cartesian(options: List[(String, Set[String])], acc: Set[Map[String, String]]): Set[Map[String, String]] =
      options match {
        case Nil => acc
        case head :: tail =>
          val next = for {
            d <- head._2
            ds <- acc
          } yield ds.updated(head._1, d)
          cartesian(tail, next)
      }

    val restricted = conjuncts.foldLeft(elements.mapValues(_.toSet)) { (options, conjunct) =>
      conjunct match {
        case x: ConstraintInList => x.restrictedValues(options)
        case x: ConstraintNegation => x.restrictedValues(options)
        case _ => throw new NotADNFException
      }
    }
    cartesian(restricted.toList, Set(Map.empty))
  }
}

case class ConstraintDisjunction(disjuncts: List[Constraint]) extends Constraint {
  override def apply(elementValues: Map[String, String]): Boolean = disjuncts.exists(_(elementValues))

  override def normalForm: Constraint = {
    val normalizedDisjuncts = disjuncts.flatMap(_.normalForm match {
      case ConstraintDisjunction(literalConjunctions) => literalConjunctions
      case x => List(x)
    })
    ConstraintDisjunction(normalizedDisjuncts)
  }

  override def generate(elements: Map[String, List[String]]): Set[Map[String, String]] =
    disjuncts.foldLeft(Set.empty[Map[String, String]]) { (combinations, conjunction) =>
      combinations ++ conjunction.generate(elements)
    }
}
