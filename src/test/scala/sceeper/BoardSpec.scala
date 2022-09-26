package sceeper

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class BoardSpec extends AnyWordSpec with ScalaCheckPropertyChecks with should.Matchers {

  // 0 1 B 1
  // 0 1 1 1
  // 0 1 1 1
  // 1 2 B 1
  // B 2 1 1
  "A Board of 4x5" when {

    val mines = Set(Location(0, 4), Location(2, 0), Location(2, 3))
    val board = new Board(4, 5, mines)

    "mines at (0,4), (2,0) and (2,4)" should {

      val locations = Table(
        ("x", "y", "expected"),
        // first Column
        (0, 0, WaterField(0)),
        (0, 1, WaterField(0)),
        (0, 2, WaterField(0)),
        (0, 3, WaterField(1)),
        (0, 4, MineField),
        // second Column
        (1, 0, WaterField(1)),
        (1, 1, WaterField(1)),
        (1, 2, WaterField(1)),
        (1, 3, WaterField(2)),
        (1, 4, WaterField(2)),
        // third Column
        (2, 0, MineField),
        (2, 1, WaterField(1)),
        (2, 2, WaterField(1)),
        (2, 3, MineField),
        (2, 4, WaterField(1)),
        // last Column
        (3, 0, WaterField(1)),
        (3, 1, WaterField(1)),
        (3, 2, WaterField(1)),
        (3, 3, WaterField(1)),
        (3, 4, WaterField(1)),
      )

      forAll(locations) { (x, y, expected) =>
        s"should yield $expected when asked for $x,$y" in {
          board.at(Location(x, y)) should equal(expected)
        }
      }
    }
  }

}
