package net.bmjames.opts.common

import scalaz.{Equal, Monoid}
import scalaz.std.option._
import scalaz.std.string._

sealed trait MatchResult
case object NoMatch extends MatchResult
case class Match(s: Option[String]) extends MatchResult

object MatchResult {
  implicit val matchResultEqual: Equal[MatchResult] =
    Equal.equal {
      case (Match(s1), Match(s2)) =>
        Equal[Option[String]].equal(s1, s2)
      case (NoMatch, NoMatch) =>
        true
      case (_, _) =>
        false
    }

  implicit val matchResultMonoid: Monoid[MatchResult] =
    new Monoid[MatchResult] {
      def zero: MatchResult = NoMatch
      def append(f1: MatchResult, f2: => MatchResult): MatchResult =
        f1 match {
          case Match(_) => f1
          case NoMatch  => f2
        }
    }
}
