package acceptance

import bank.orders.MatchedOrderChooser._
import bank.orders.Matcher._
import bank.orders._
import bank.values._
import cucumber.api.junit.Cucumber
import cucumber.api.scala.{EN, ScalaDsl}
import org.junit.runner.RunWith
import org.scalatest.Matchers

class OrdersStepDefs extends ScalaDsl with EN with Matchers {
  private implicit val orderMatching: OrderMatching = matches
  private implicit val orderChoosing: OrderChoosing = choose

  private var openOrders = OpenOrders.empty
  private var executedOrders = ExecutedOrders.empty

  When( """^we receive an order: (\S+) (\d+) (\S+) @ (\S+) (\S+)$""") {
    (direction: String, quantity: Quantity, ric: String, price: String, user: String) =>
      val order = Order(asDirection(direction), RIC(ric), quantity, BigDecimal(price), User(user))
      val (newOpenOrders, executedOrder) = OpenOrders.addNewOrder(openOrders, order)
      openOrders = newOpenOrders
      executedOrders = executedOrder.fold(executedOrders)(ExecutedOrders.add(executedOrders, _))
  }

  Then( """^the Open (\S+) (\S+) interest is (\d+) @ (\S+)$""") {
    (ric: String, direction: String, quantity: Quantity, price: String) =>
      openOrders.openInterest(asDirection(direction), RIC(ric)) shouldBe Set(OpenInterest(quantity, BigDecimal(price)))
  }

  Then( """^the Open (\S+) (\S+) interest is (\d+) @ (\S+) and (\d+) @ (\S+)$""") {
    (ric: String, direction: String, quantity1: Quantity, price1: String, quantity2: Quantity, price2: String) =>
      openOrders.openInterest(asDirection(direction), RIC(ric)) shouldBe
        Set(OpenInterest(quantity1, BigDecimal(price1)), OpenInterest(quantity2, BigDecimal(price2)))
  }

  Then( """^the Executed quantity for (\S+), (\S+) is ([-]?\d+)$""") {
    (ric: String, user: String, quantity: Quantity) =>
      executedOrders.executedQuantity(RIC(ric), User(user)) shouldBe quantity
  }

  Then( """^the Average (\S+) exec\. price is (\S+)$""") {
    (ric: String, price: String) =>
      executedOrders.averageExecutionPrice(RIC(ric)) shouldBe BigDecimal(price).setScale(4)
  }

  Then( """^there is zero Open (\S+) (\S+) interest$""") {
    (ric: String, direction: String) =>
      openOrders.openInterest(asDirection(direction), RIC(ric)) shouldBe 'empty
  }

  private def asDirection(direction: String): Direction = if (direction == "SELL") Sell else Buy
}

@RunWith(classOf[Cucumber])
class AcceptanceTests
