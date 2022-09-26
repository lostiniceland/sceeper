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
  case GameOver(mines: Set[Location]) extends ActionResult


class Sceeper private[sceeper](val board: Board):

  private[this] var opened = Set[WaterField]()
  private[this] var flagged = Set[Location]()
  private[this] var gameOver = false

  private[sceeper] def openedFields = opened

  private[sceeper] def flaggedFields = flagged

  def execute(action: Action): ActionResult =

    import Action.*
    import ActionResult.*

    if(gameOver)
      GameOver(board.mines)
    else
      action match
        case Open(l) =>
          board.at(l) match
            case MineField => gameOver = true; ActionResult.GameOver(board.mines)
            case w: WaterField =>
              val o = clearSea(w)
              opened = opened ++ o
              Opened(o)
        case ToggleFlag(l) =>
          if flagged.contains(l) then
            flagged -= l
            UnFlagged
          else
            flagged += l
            Flagged

  private def clearSea(w: WaterField): Set[WaterField] =
    @tailrec
    def clear(locations: Set[Location], acc: List[WaterField], visited: Set[Location]): List[WaterField] =
      if(locations.isEmpty || locations.subsetOf(visited))
        acc
      else
        val surroundingWaters = locations.map(board.at).collect{case w: WaterField => w}
        clear(
          surroundingWaters.filter(w => w.proximityMines == 0).flatMap(w => board.neighborsOf(w.location)),
          acc ++ surroundingWaters,
          visited ++ locations)

    clear(board.neighborsOf(w.location), List(w), Set(w.location)).toSet

end Sceeper

object Sceeper:

  def apply(width: Int, height: Int, countMines: Int): Sceeper =
    val board = Board(width, height, countMines)
    new Sceeper(board)

end Sceeper
