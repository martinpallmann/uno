package uno

sealed trait Card

object Card {
  case class Number(color: Color, digit: Digit) extends Card
  case class Skip(color: Color) extends Card
  case class Reverse(color: Color) extends Card
  case class TakeTwo(color: Color) extends Card
  case object TakeFour extends Card
  case object ChooseColor extends Card

  sealed trait Color

  object Color {
    case object Red extends Color
    case object Green extends Color
    case object Blue extends Color
    case object Yellow extends Color
  }

  sealed trait Digit

  object Digit {
    case object Zero extends Digit
    case object One extends Digit
    case object Two extends Digit
    case object Three extends Digit
    case object Four extends Digit
    case object Five extends Digit
    case object Six extends Digit
    case object Seven extends Digit
    case object Eight extends Digit
    case object Nine extends Digit
  }
}
