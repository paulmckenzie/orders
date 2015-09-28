package cs.orders

import cs.orders.ExecutedOrders.add
import cs.orders.TestValues.{VodaphoneLondon, wowbagger, zarniwoop}
import cs.values._
import org.mockito.Mockito.mock
import org.scalatest.{FreeSpec, Matchers}

class ExecutedOrdersTest extends FreeSpec with Matchers {

  "executed orders" - {
    "can be empty" in {
      ExecutedOrders.empty.orders shouldBe Nil
    }

    "can have a new executed order added" in {
      val executedOrder = anExecutedOrder(VodaphoneLondon, 100.0, 10, zarniwoop, wowbagger)
      add(ExecutedOrders.empty, executedOrder) shouldBe ExecutedOrders(executedOrder :: Nil)
    }

    "knows average execution price for a given RIC" in {
      val startingExecutedOrders = ExecutedOrders.empty

      val eo1 = anExecutedOrder(VodaphoneLondon, 100.0, 10, zarniwoop, wowbagger)
      val executedOrdersAfterFirstOrder = add(startingExecutedOrders, eo1)
      executedOrdersAfterFirstOrder.averageExecutionPrice(VodaphoneLondon) shouldBe 100.0

      val eo2 = anExecutedOrder(VodaphoneLondon, 140.0, 20, wowbagger, zarniwoop)
      val executedOrdersAfterSecondOrder = add(executedOrdersAfterFirstOrder, eo2)
      executedOrdersAfterSecondOrder.averageExecutionPrice(VodaphoneLondon) shouldBe 126.6667
    }

    "knows executed quantity for a given RIC and user" in {
      val startingExecutedOrders = ExecutedOrders.empty

      val eo1 = anExecutedOrder(VodaphoneLondon, 100.0, 10, zarniwoop, wowbagger)
      val executedOrdersAfterFirstOrder = add(startingExecutedOrders, eo1)
      executedOrdersAfterFirstOrder.executedQuantity(VodaphoneLondon, zarniwoop) shouldBe 10
      executedOrdersAfterFirstOrder.executedQuantity(VodaphoneLondon, wowbagger) shouldBe -10

      val eo2 = anExecutedOrder(VodaphoneLondon, 140.0, 20, wowbagger, zarniwoop)
      val executedOrdersAfterSecondOrder = add(executedOrdersAfterFirstOrder, eo2)
      executedOrdersAfterSecondOrder.executedQuantity(VodaphoneLondon, zarniwoop) shouldBe -10
      executedOrdersAfterSecondOrder.executedQuantity(VodaphoneLondon, wowbagger) shouldBe 10
    }
  }

  private def anExecutedOrder(london: RIC, price: Price, quantity: Quantity, buyer: User, seller: User) =
    ExecutedOrder(
      buy = Order(Buy, VodaphoneLondon, quantity, price, buyer),
      sell = Order(Sell, VodaphoneLondon, quantity, price, seller),
      price = price)
}
