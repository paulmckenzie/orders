package bank.orders

import bank.values._

case class OpenOrders(orders: List[Order]) {
  def openInterest(direction: Direction, ric: RIC): Set[OpenInterest] = {
    val ordersForDirectionAndRIC = orders.filter(o => o.direction == direction && o.ric == ric)
    val ordersGroupedByPrice = ordersForDirectionAndRIC.groupBy(_.price)
    ordersGroupedByPrice.map {
      case (price, theseOrders) => OpenInterest(theseOrders.map(_.quantity).sum, price)
    }.toSet
  }
}

object OpenOrders {
  val empty = OpenOrders(List.empty)

  def addNewOrder(openOrders: OpenOrders,
                  newOrder: Order)
                 (implicit matcher: OrderMatching,
                  chooser: OrderChoosing): (OpenOrders, Option[ExecutedOrder]) = {
    val matchingOrders = openOrders.orders.filter(matcher(newOrder, _))

    if (matchingOrders.isEmpty)
      matchFail(openOrders, newOrder)
    else
      matchSuccess(openOrders, newOrder, chooser(newOrder, matchingOrders))
  }

  private def matchFail(openOrders: OpenOrders, newOrder: Order) = {
    val newOpenOrders = openOrders.copy(orders = openOrders.orders :+ newOrder)
    val executedOrder = None
    (newOpenOrders, executedOrder)
  }

  private def matchSuccess(openOrders: OpenOrders, newOrder: Order, matchedOrder: Order) = {
    val newOpenOrders = openOrders.copy(orders = openOrders.orders diff List(matchedOrder))

    val executedOrder = if (matchedOrder.direction == Buy)
      ExecutedOrder(matchedOrder, newOrder, newOrder.price)
    else
      ExecutedOrder(newOrder, matchedOrder, newOrder.price)

    (newOpenOrders, Option(executedOrder))
  }
}
