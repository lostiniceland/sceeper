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
        ("x", "y", "isMine", "proximityMines"),
        // first Column
        (0, 0, false, 0),
        (0, 1, false, 0),
        (0, 2, false, 0),
        (0, 3, false, 1),
        (0, 4, true, 0),
        // second Column
        (1, 0, false, 1),
        (1, 1, false, 1),
        (1, 2, false, 1),
        (1, 3, false, 2),
        (1, 4, false, 2),
        // third Column
        (2, 0, true, 0),
        (2, 1, false, 1),
        (2, 2, false, 1),
        (2, 3, true, 0),
        (2, 4, false, 1),
        // last Column
        (3, 0, false, 1),
        (3, 1, false, 1),
        (3, 2, false, 1),
        (3, 3, false, 1),
        (3, 4, false, 1),
      )

      forAll(locations) { (x, y, isMine, proximityMines) =>
        val expected = if isMine then MineField else WaterField(proximityMines, Location(x,y))
        s"should yield $expected when asked for $x,$y" in {
          board.at(Location(x, y)) should equal(expected)
        }
      }
    }
  }

}
