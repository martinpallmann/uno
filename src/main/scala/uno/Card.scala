package uno

sealed trait Card

object Card {

  case class Number(color: Color, digit: Digit) extends Card {
    override def toString: String = s"$color$digit"
  }
  case class Skip(color: Color) extends Card {
    override def toString: String = s"${color}S"
  }
  case class Reverse(color: Color) extends Card {
    override def toString: String = s"${color}R"
  }
  case class DrawTwo(color: Color) extends Card {
    override def toString: String = s"${color}D"
  }
  case object WildDrawFour extends Card {
    override def toString: String = "WD"
  }
  case object Wild extends Card {
    override def toString: String = "WW"
  }

  def unapply(s: String): Option[Card] = {
    if (s.length != 2) None
    else (s.charAt(0), s.charAt(1)) match {
      case ('W', 'W') => Some(Wild)
      case ('W', 'D') => Some(WildDrawFour)
      case ( c,  'D') => Color(c).map(DrawTwo)
      case ( c,  'R') => Color(c).map(Reverse)
      case ( c,  'S') => Color(c).map(Skip)
      case ( c,   d ) => for {
        c1 <- Color(c)
        d1 <- Digit(d)
      } yield Number(c1, d1)
    }
  }

  def apply(s: String): Option[Card] = ({
    case Card(c) => c
  }: PartialFunction[String, Card]).lift(s)

  sealed trait Color

  object Color {

    case object Red extends Color {
      override def toString: String = "R"
    }
    case object Green extends Color {
      override def toString: String = "G"
    }
    case object Blue extends Color {
      override def toString: String = "B"
    }
    case object Yellow extends Color {
      override def toString: String = "Y"
    }

    def apply(c: Char): Option[Color] = ({
      case 'R' => Red
      case 'G' => Green
      case 'B' => Blue
      case 'Y' => Yellow
    }: PartialFunction[Char, Color]).lift(c)
  }

  sealed trait Digit

  object Digit {

    case object Zero extends Digit {
      override def toString: String = "0"
    }
    case object One extends Digit {
      override def toString: String = "1"
    }
    case object Two extends Digit {
      override def toString: String = "2"
    }
    case object Three extends Digit {
      override def toString: String = "3"
    }
    case object Four extends Digit {
      override def toString: String = "4"
    }
    case object Five extends Digit {
      override def toString: String = "5"
    }
    case object Six extends Digit {
      override def toString: String = "6"
    }
    case object Seven extends Digit {
      override def toString: String = "7"
    }
    case object Eight extends Digit {
      override def toString: String = "8"
    }
    case object Nine extends Digit {
      override def toString: String = "9"
    }

    def apply(c: Char): Option[Digit] = ({
      case '0' => Zero
      case '1' => One
      case '2' => Two
      case '3' => Three
      case '4' => Four
      case '5' => Five
      case '6' => Six
      case '7' => Seven
      case '8' => Eight
      case '9' => Nine
    }: PartialFunction[Char, Digit]).lift(c)
  }
}
