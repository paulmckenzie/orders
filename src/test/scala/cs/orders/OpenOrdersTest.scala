package cs.orders

import cs.orders.OpenOrders.addNewOrder
import cs.orders.TestValues.{VodaphoneLondon, wowbagger, zarniwoop}
import cs.values._
import org.mockito.Mockito.mock
import org.scalatest.{FreeSpec, Matchers}

class OpenOrdersTest extends FreeSpec with Matchers {

  "Adding an order" - {
    val anOrder = Order(Buy, VodaphoneLondon, 1000, 100.2, wowbagger)
    val anotherOrder = Order(Sell, VodaphoneLondon, 1000, 100.2, zarniwoop)

    "to OpenOrders" - {
      implicit val matchAlways = (order1: Order, order2: Order) => true
      implicit val chooseFirst = (newOrder:Order, orders: List[Order]) => orders.head

      "with no open orders adds the new order and doesn't find an executed order" in {
        val (updatedOpenOrders, executedOrder) = addNewOrder(OpenOrders.empty, anOrder)
        updatedOpenOrders shouldBe OpenOrders(List(anOrder))
        executedOrder shouldBe None
      }

      "with one open order" - {

        "that doesn't match adds the new order and doesn't find an executed order" in {
          val matchNever = (order1: Order, order2: Order) => false
          val (updatedOpenOrders, executedOrder) = addNewOrder(OpenOrders(List(anotherOrder)), anOrder)(matchNever, chooseFirst)
          updatedOpenOrders shouldBe OpenOrders(List(anotherOrder, anOrder))
          executedOrder shouldBe None
        }

        "that matches creates an executed order and removes the matching order" in {
          val (updatedOpenOrders, executedOrder) = addNewOrder(OpenOrders(List(anotherOrder)), anOrder)
          updatedOpenOrders shouldBe OpenOrders.empty
          executedOrder shouldBe Option(ExecutedOrder(anOrder, anotherOrder, anOrder.price))
        }
      }

      "with several matching open orders chooses the best and emits an optional executed order" in {
        val matchingSells = mock(classOf[Order]) :: mock(classOf[Order]) :: mock(classOf[Order]) :: Nil
        val (updatedOpenOrders, executedOrder) = addNewOrder(OpenOrders(matchingSells), anOrder)
        updatedOpenOrders shouldBe OpenOrders(matchingSells.tail)
        executedOrder shouldBe Option(ExecutedOrder(anOrder, matchingSells.head, anOrder.price))
      }
    }
  }

  "Open Interest" - {

    "with no open orders is an empty Set" in {
      OpenOrders.empty.openInterest(Buy, VodaphoneLondon) shouldBe 'empty
    }

    "is the total for all open orders for the given RIC and direction at each  point" in {
      val buy1000VodaAt100Dot2 = Order(Buy, VodaphoneLondon, 1000, 100.2, wowbagger)
      val buy500VodaAt100Dot2 = Order(Buy, VodaphoneLondon, 5000, 100.2, wowbagger)
      val buy5000VodaAt100Dot3 = Order(Buy, VodaphoneLondon, 5000, 100.3, wowbagger)
      val openOrders = OpenOrders(List(buy1000VodaAt100Dot2, buy500VodaAt100Dot2, buy5000VodaAt100Dot3))
      openOrders.openInterest(Buy, VodaphoneLondon) shouldBe Set(OpenInterest(5000, 100.3), OpenInterest(6000, 100.2))
    }
  }
}
