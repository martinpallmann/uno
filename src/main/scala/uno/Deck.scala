package uno

import uno.Card.Color.Red
import uno.Card.Reverse
import Predef.intWrapper

case class Deck(cards: List[Card]) {

  def draw: (Card, Deck) = (cards.headOption.getOrElse(Reverse(Red)), Deck(cards.drop(1)))
}

object Deck {

  type Shuffle = (Deck) => Deck

  val colors: List[Char] = 'R' :: 'G' :: 'B' :: 'Y' :: Nil

  def apply(): Deck = {
    val nc = for {
      c <- colors
      d <- (1 to 19).toList
      x <- Card(s"$c${d % 10}").toList
    } yield x
    val sc = for {
      c <- colors
      t <- 'D' :: 'R' :: 'S' :: Nil
      _ <- (1 to 2).toList
      x <- Card(s"$c$t").toList
    } yield x
    val wc = for {
      t <- 'W' :: 'D' :: Nil
      _ <- (1 to 4).toList
      x <- Card(s"W$t").toList
    } yield x
    Deck(nc ++ sc ++ wc)
  }
}
