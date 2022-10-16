package sceeper.solver

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import sceeper.ActionResult.{Flagged, Victory}
import sceeper.{Board, Location, Sceeper}
import sceeper.solver.Solver

class SolverSpec extends AnyWordSpec {

  // 0 1 B 1
  // 0 1 1 1
  // 0 1 1 1
  // 1 2 B 1
  // B 2 1 1
  "A Board of 4x5 with mines at (0,4), (2,0) and (2,4)" when {

    val mines = Set(Location(0, 4), Location(2, 0), Location(2, 3))
    val board = new Board(4, 5, mines)

    "using the automatic solver" should {
      val solver = new Solver(new Sceeper(board))
//      solver.solve should matchPattern { case Victory => }
    }
  }

}
