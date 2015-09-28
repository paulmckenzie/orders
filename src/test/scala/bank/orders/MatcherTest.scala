package bank.orders


import org.scalatest.{FreeSpec, Matchers}
import bank.values._

class MatcherTest extends FreeSpec with Matchers {
  "An order" - {
    val buy = Order(Buy, RIC("VOD.L"), 1000, 100.0, User("user1"))
    val sell = buy.copy(direction = Sell, user = User("user2"))

    "doesn't match" - {

      "another order" - {

        "with the same direction" in {
          Matcher.matches(buy, buy.copy()) shouldBe false
        }

        "with a different RIC" in {
          Matcher.matches(buy, sell.copy(ric = RIC("L.DOV"))) shouldBe false
        }

        "with a different quantity" in {
          Matcher.matches(buy, sell.copy(quantity = 1001)) shouldBe false
        }

        "with a buy price greater than the sell price" in {
          Matcher.matches(buy, sell.copy(price = 101.0)) shouldBe false
        }
      }

      "does match" - {
        "an order with the opposite direction" in {
          Matcher.matches(buy, sell) shouldBe true
        }

        "with a sell price less than the buy price" in {
          Matcher.matches(buy, sell.copy(price = 99.0)) shouldBe true
        }
      }
    }
  }
}
