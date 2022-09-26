package sceeper

import org.scalatest.matchers.should
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class LocationSpec extends AnyPropSpec with ScalaCheckPropertyChecks with should.Matchers {

  property("validating if a location is within a given dimension should produce the correct result"){

    val board = Board(2, 2, 1)
    val locations = Table(
      ("x", "y", "expected"),
      (0, 0, true),
      (1, 1, true),
      (Integer.MAX_VALUE, 0, false),
      (0, Integer.MAX_VALUE, false)
    )

    forAll(locations) { (x, y, expected) =>
      board.isValid(Location(x, y)) should equal(expected)
    }
  }

  property("creating a location with negative coordinates produces an IllegalArgumentException"){

    val invalidCoordinates = Table(
      ("x", "y"),
      (0, -1),
      (-1, 0)
    )
    forAll(invalidCoordinates) { (x, y) =>
      a [IllegalArgumentException] should be thrownBy{
        Location(x,y)
      }
    }
  }

  //  property("a location not on the edge yields 8 results"){
  //    val locations
  //  }

  property("location-neighbors are calculated depending on edge/corner"){

    val board = Board(10, 10, 1)
    import org.scalacheck.Gen

    val coordinates = for
      x <- Gen.choose(0, board.width)
      y <- Gen.choose(0, board.height)
    yield (x, y)

    forAll(coordinates) { (x: Int, y: Int) =>
      whenever (x > 0 && y > 0 && x < board.width && y < board.height) {
        val l = Location(x, y)

        if board.isCorner(l) then
          board.neighborsOf(l).size should equal(3)
        else if board.isEdge(l) then
          board.neighborsOf(l).size should equal(5)
        else
          board.neighborsOf(l).size should equal(8)
      }
    }
  }
}
