package sceeper

import java.security.KeyStore.TrustedCertificateEntry
import scala.annotation.tailrec
import scala.util.{Random, Try}


enum Action:
  case Open(location: Location)
  case ToggleFlag(location: Location)

enum ActionResult:
  case Opened(locations: Set[WaterField])
  case Flagged
  case UnFlagged
  case Victory(mines: Set[Location])
  case GameOver(mines: Set[Location])


class Sceeper private[sceeper](val board: Board):

  private[sceeper] var opened = Set[WaterField]()
  private[sceeper] var flagged = Set[Location]()
  private[this] var gameOver = false

  def execute(action: Action): ActionResult =

    import Action.*
    import ActionResult.*

    if(gameOver)
      GameOver(board.mines)
    else
      action match
        case Open(l) =>
          board.at(l) match
            case MineField => gameOver = true; GameOver(board.mines)
            case w: WaterField =>
              opened = opened ++ openSurroundings(w)
              if board.hasBeenCleared(opened) then
                Victory(board.mines)
              else
                Opened(opened)
        case ToggleFlag(l) =>
          if flagged.contains(l) then
            flagged -= l
            UnFlagged
          else
            flagged += l
            Flagged

  /**
   * Main Algorithm: recursively checks the neighbors of the given [[WaterField]], if those are also [[WaterField]]s.
   * All fields with 0 proximity-mines are safe to open up, and their neighbors are checked. The recursion stops, when
   * no more fields are found as input, or if they have already been visited.
   * @param w starting [[WaterField]]
   * @return all fields starting from [[w]] that are safe to open and their neighboring [[WaterFields]] with a proximity-mines > 0
   */
  private def openSurroundings(w: WaterField): Set[WaterField] =
    @tailrec
    def open(locations: Set[Location], acc: List[WaterField], visited: Set[Location]): List[WaterField] =
      if(locations.isEmpty || locations.subsetOf(visited))
        acc
      else
        val waterLocations = locations.map(board.at).collect{case w: WaterField => w}
        open(
          waterLocations.filter(w => w.proximityMines == 0).flatMap(w => board.neighborsOf(w.location)),
          acc ++ waterLocations,
          visited ++ locations)

    open(board.neighborsOf(w.location), List(w), Set(w.location)).toSet

end Sceeper

object Sceeper:

  def apply(width: Int, height: Int, countMines: Int): Sceeper =
    val board = Board(width, height, countMines)
    new Sceeper(board)

end Sceeper
