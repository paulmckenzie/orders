package cs.orders

import cs.values._
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Random

class MatchedOrderChooserTest extends FreeSpec with Matchers {
  "Choosing a matched order from a list" - {

    "where the new order" - {

      "is a buy" - {

        "and the matched orders" - {

          "is an empty list then will throw an illegal argument exception " in {
            intercept[IllegalArgumentException] {
              MatchedOrderChooser.choose(aBuyOrder(), List.empty)
            }
          }

          "is a list of one order then will return that order" in {
            val matchedOrders = List(aSellOrder())
            MatchedOrderChooser.choose(aBuyOrder(), matchedOrders) shouldBe matchedOrders.head
          }

          "is a list of many orders" - {
            "with same price then will return the earliest order" in {
              val matchingOrders = aSellOrder() :: aSellOrder() :: aSellOrder() :: Nil
              MatchedOrderChooser.choose(aBuyOrder(), matchingOrders) shouldBe matchingOrders.head
            }

            "with different prices will return the order with the lowest price" in {
              val matchingOrders = aSellOrder() :: aSellOrder(price = 99.0) :: aSellOrder(price = 101.0) :: Nil
              MatchedOrderChooser.choose(aBuyOrder(), matchingOrders).price shouldBe 99.0
            }

          }
        }
      }
      "is a sell" - {

        "and the matched orders" - {

          "is an empty list then will throw an illegal argument exception " in {
            intercept[IllegalArgumentException] {
              MatchedOrderChooser.choose(aSellOrder(), List.empty)
            }
          }

          "is a list of one order then will return that order" in {
            val matchedOrders = List(aBuyOrder())
            MatchedOrderChooser.choose(aSellOrder(), matchedOrders) shouldBe matchedOrders.head
          }

          "is a list of many orders" - {
            "with same price then will return the earliest order" in {
              val matchingOrders = aBuyOrder() :: aBuyOrder() :: aBuyOrder() :: Nil
              MatchedOrderChooser.choose(aSellOrder(), matchingOrders) shouldBe matchingOrders.head
            }

            "with different prices will return the order with the highest price" in {
              val matchingOrders = aBuyOrder() :: aBuyOrder(price = 99.0) :: aBuyOrder(price = 101.0) :: Nil
              MatchedOrderChooser.choose(aSellOrder(), matchingOrders).price shouldBe 101.0
            }
          }
        }
      }
    }
  }

  private def aBuyOrder(price: Price = 100.0) = anOrder(Buy, price)

  private def aSellOrder(price: Price = 100.0) = anOrder(Sell, price)

  private def anOrder(direction: Direction, price: Price) = Order(direction, RIC("VOD.L"), 1000, price, User("user" + Random.nextLong()))
}
