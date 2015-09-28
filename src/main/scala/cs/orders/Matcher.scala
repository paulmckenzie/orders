package cs.orders

import cs.values.{Buy, Order, Sell}

object Matcher {

  def matches(anOrder: Order, anotherOrder: Order): Boolean =
    (anOrder.direction, anotherOrder.direction) match {
      case (Buy, Sell) => matchBuySell(anOrder, anotherOrder)
      case (Sell, Buy) => matchBuySell(anotherOrder, anOrder)
      case _ => false
    }

  private def matchBuySell(buy: Order, sell: Order) = buy.ric == sell.ric && buy.quantity == sell.quantity && sell.price <= buy.price
}
