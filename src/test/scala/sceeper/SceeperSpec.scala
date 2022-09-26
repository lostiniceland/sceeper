package sceeper

import org.scalatest.*
import org.scalatest.Inside.inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*
import org.scalatest.matchers.should.*
import org.scalatest.propspec.AnyPropSpec
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.collection.immutable.*


class SweeperGameSpec extends AnyFlatSpec with Matchers {

  // FIXME TESTs überarbeiten
  "A Board" must "have unique mine locations" in {
    val board = Board(10, 10, 99)
    val duplicates = board.mines.groupBy(identity).collect { case (x, ys) if ys.toSeq.lengthCompare(1) > 0 => x }
    assert(duplicates.isEmpty)
  }

  it must "have all mines within its dimension" in {
    assertThrows[IllegalArgumentException] {
      val okMine = Location(0, 0)
      val misplacedMine = Location(20, 20)
      new Board(10, 10, Set(okMine, misplacedMine))
    }
  }

  it must "produce an Exception when NO mine is present" in {
    assertThrows[IllegalArgumentException] {
      Board(10, 10, 0)
    }
  }

  it must "produce an Exception when ONLY mines are created" in {
    assertThrows[IllegalArgumentException] {
      Board(10, 10, 100)
    }
  }

//  it must "produce an Exception when a mine is hit with an Open action" in {
//    val board = new Board(Dimension(2, 2), Set(Location(0, 0)))
//    board.execute(Open(Location(0, 0))) should matchPattern { case GameOver(_) => }
//  }

  it must "produce an Exception when the size is smaller than 2x2" in  {
    assertThrows[IllegalArgumentException] {
      Sceeper(1, 2, 1)
    }
  }
}

class SweeperStateSpec extends AnyFlatSpec with Matchers {

  def fixture = new Sceeper(new Board(2, 2, Set(Location(0,0))))

  "Flagging a field" should "add the location to the internal state" in {
    val sut = fixture
    val toFlag = Location(0,0)
    sut.execute(ToggleFlag(toFlag)) should matchPattern { case Flagged => }
    assert(sut.flaggedFields.contains(toFlag))
  }

  it should "clear the state on removal" in {
    val sut = fixture
    val toFlag = Location(0, 0)
    sut.execute(ToggleFlag(toFlag)) should matchPattern { case Flagged => }
    sut.execute(ToggleFlag(toFlag)) should matchPattern { case UnFlagged => }
    assert(!sut.flaggedFields.contains(toFlag))
  }

  it should "after hitting the first mine, all actions must return GameOver" in {
    val sut = fixture
    sut.execute(Open(Location(0,0))) should matchPattern { case GameOver(_) => }
    sut.execute(ToggleFlag(Location(1,1))) should matchPattern { case GameOver(_) => }
    sut.execute(Open(Location(1,1))) should matchPattern { case GameOver(_) => }
  }
}

class SweeperXSpec extends AnyWordSpec with ScalaCheckPropertyChecks with should.Matchers {

  // 0 1 B 1
  // 0 1 1 1
  // 0 1 1 1
  // 1 2 B 1
  // B 2 1 1
  "A Board of 4x5" when {
    
    val mines = Set(Location(0, 4), Location(2, 0), Location(2, 3))
    val board = new Board(4, 5, mines)
    new Sceeper(board)

    "mines at (0,4), (2,0) and (2,4)" should {
      

      val locations = Table(
        ("x", "y", "expected"),
        // first Column
        (0, 0, OpenedField(Location(0,0), 0))),
        (0, 1, Set(OpenedField(Location(0,0), 0))),
        (0, 2, WaterField(0)),
        (0, 3, WaterField(1)),
        (0, 4, MineField),
        
      )

      forAll(locations) { (x, y, expected) =>
        s"should yield $expected when asked for $x,$y" in {
          board.at(Location(x, y)) should equal(expected)
        }
      }
    }
  }

}


