Feature: Calculate the open interest, the executed quantity and the average execution price

  Scenario: The fully worked example
    When we receive an order: SELL 1000 VOD.L @ 100.2 User1
    Then the Open VOD.L SELL interest is 1000 @ 100.2
    And the Executed quantity for VOD.L, User1 is 0

    # matches existing buy order, executed @ 100.2
    When we receive an order: BUY 1000 VOD.L @ 100.2 User2
    Then the Executed quantity for VOD.L, User1 is -1000
    And the Executed quantity for VOD.L, User2 is 1000
    And the Average VOD.L exec. price is 100.2000
    Then there is zero Open VOD.L SELL interest

    When we receive an order: BUY 1000 VOD.L @ 99 User1
    Then the Open VOD.L BUY interest is 1000 @ 99

    When we receive an order: BUY 1000 VOD.L @ 101 User1
    Then the Open VOD.L BUY interest is 1000 @ 99 and 1000 @ 101

    When we receive an order: SELL 500 VOD.L @ 102 User2
    Then the Open VOD.L SELL interest is 500 @ 102

    # matches existing SELL @ 102, executed @ 103
    When we receive an order: BUY 500 VOD.L @ 103 User1
    Then the Executed quantity for VOD.L, User1 is -500
    And the Executed quantity for VOD.L, User2 is 500
    And the Average VOD.L exec. price is 101.1333
    Then there is zero Open VOD.L SELL interest

    # matches existing BUY @ 101, executed @ 98
    When we receive an order: SELL 1000 VOD.L @ 98 User2
    Then the Executed quantity for VOD.L, User1 is 500
    And the Executed quantity for VOD.L, User2 is -500
    And the Average VOD.L exec. price is 99.8800
    Then the Open VOD.L BUY interest is 1000 @ 99


