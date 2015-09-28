package bank.orders

import bank.values.{Buy, Order}

object MatchedOrderChooser {
  def choose(newOrder: Order, matchingOrders: List[Order]): Order = matchingOrders match {
    case Nil => throw new IllegalArgumentException("Cannot choose from an empty list of matching orders")
    case x :: Nil => x
    case head :: tail if tail.forall(_.price == head.price) => head
    case _ => if (newOrder.direction == Buy) matchingOrders.sortBy(_.price).head else matchingOrders.sortBy(_.price).reverse.head
  }
}
