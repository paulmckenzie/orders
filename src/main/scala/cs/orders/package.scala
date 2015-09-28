package cs

import cs.values.Order

package object orders {
  type OrderMatching = (Order, Order) => Boolean
  type OrderChoosing = (Order, List[Order]) => Order

}
