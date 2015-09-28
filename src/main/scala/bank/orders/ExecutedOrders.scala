package bank.orders

import bank.values._

import scala.math.BigDecimal.RoundingMode

case class ExecutedOrders(orders: List[ExecutedOrder]) {
  def executedQuantity(ric: RIC, user: User): Quantity = {
    def isIncluded(order: Order) = order.ric == ric && order.user == user
    val buyQuantity = orders.filter(eo => isIncluded(eo.buy)).map(_.buy.quantity).sum
    val sellQuantity = orders.filter(eo => isIncluded(eo.sell)).map(_.sell.quantity).sum
    buyQuantity - sellQuantity
  }

  def averageExecutionPrice(ric: RIC): Price = {
    val relevantOrders = orders.filter(_.buy.ric == ric)
    val priceTimesQuantity = relevantOrders.map(eo => eo.price * eo.buy.quantity).sum
    val quantity: BigDecimal = relevantOrders.map(_.buy.quantity).sum
    (priceTimesQuantity / quantity).setScale(4, RoundingMode.HALF_UP)
  }
}

object ExecutedOrders {
  def add(existingOrders: ExecutedOrders, executedOrder: ExecutedOrder): ExecutedOrders = existingOrders.copy(existingOrders.orders :+ executedOrder)

  val empty = ExecutedOrders(List.empty)
}