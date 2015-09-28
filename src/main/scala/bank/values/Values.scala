package bank.values

sealed trait Direction

object Buy extends Direction

object Sell extends Direction

case class User(name: String)

case class RIC(symbol: String)

case class Order(direction: Direction, ric: RIC, quantity: Quantity, price: Price, user: User)

case class ExecutedOrder(buy: Order, sell: Order, price: Price)

case class OpenInterest(quantity: Quantity, price: Price)