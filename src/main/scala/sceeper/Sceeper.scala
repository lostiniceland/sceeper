package sceeper

import sceeper.Game.New
import sceeper.Location
import sceeper.Sceeper.Action.Open

import scala.annotation.tailrec
import scala.util.Random

type Locations = Set[Location]

case class Dimensions(width: Int, height: Int)


sealed trait Field
case class WaterField(proximityMines: Int, location: Location) extends Field
case object MineField extends Field

enum Game:
  case New(width: Int, height: Int, countMines: Int)
  case Running(opened: Set[WaterField], flagged: Locations, private[sceeper] val board: Board, private[sceeper] val mines: Locations)
  case Won(mines: Locations, correctFlags: Locations, wrongFlags: Locations)
  case Lost(mine: Location, otherMines: Locations, correctFlags: Locations, wrongFlags: Locations)

object Game:

  private[sceeper] def first(game: Game.New, firstOpened: Location): Game.Running =
    val mines = randomMines(game.countMines, game.width, game.height, firstOpened)
    val running: Game.Running = Game.Running(Set.empty, Set.empty, Board(game.width, game.height), mines)
    val openedSurroundings = openSurroundings(running, WaterField(0, firstOpened)) // TODO
    running.copy(opened = openedSurroundings)


  private[sceeper] def flagged(game: Running, flag: Location): Game.Running =
    if !game.flagged.contains(flag) then
      game.copy(flagged = game.flagged + flag)
    else
      game.copy(flagged = game.flagged - flag)

  private[sceeper] def opened(game: Running, open: WaterField): Game.Running =
    val openedSurroundings = openSurroundings(game, open)
    game.copy(opened = game.opened ++ openedSurroundings)

  private[sceeper] def lost(game: Running, triggered: Location): Game.Lost =
    val correctFlags = game.flagged // TODO
    val wrongFlags = Set.empty[Location] // TODO
    Lost(triggered, game.mines - triggered, correctFlags, wrongFlags)

  private[sceeper] def won(game: Running): Game.Won =
    val correctFlags = game.flagged // TODO
    val wrongFlags = Set.empty[Location] // TODO
    Won(game.mines, correctFlags, wrongFlags)

  /**
   * Checks if "at" the location is a [[MineField]] or [[WaterField]].
   * In case of a WaterField, the number of mines on neighboring fields is returned.
   *
   * @param l the location to look at
   * @return MineField or WaterField
   */
  private[sceeper] def test(game: Game.Running, l: Location): Field =
    if game.mines.contains(l) then
      MineField
    else
      WaterField(game.board.neighborsOf(l).count(l => game.mines.contains(l)), l)

  private def openSurroundings(game: Game.Running, w: WaterField): Set[WaterField] =
    @tailrec
    def open(locations: Set[Location], acc: List[WaterField], visited: Set[Location]): List[WaterField] =
      if (locations.isEmpty || locations.subsetOf(visited))
        acc
      else
        val waterLocations = locations.map(l => Game.test(game, l)).collect { case w: WaterField => w }
        open(
          waterLocations.filter(w => w.proximityMines == 0).flatMap(w => game.board.neighborsOf(w.location)),
          acc ++ waterLocations,
          visited ++ locations)

    open(game.board.neighborsOf(w.location), List(w), Set(w.location)).toSet
  private def randomMines(count: Int, maxX: Int, maxY: Int, firstOpened: Location): Locations =
    val randomGenerator = Random()

    def getRandomElement(seq: collection.mutable.Buffer[Location]): Location =
      val element = seq(randomGenerator.nextInt(seq.length))
      seq -= element
      element

    val allMines =
      (for
        x <- 0 until maxX
        y <- 0 until maxY
      yield Location(x, y)).toBuffer

    (1 to count).map(_ => getRandomElement(allMines.-=(firstOpened))).toSet

end Game

object Sceeper {

  enum Action:
    case Open(location: Location)
    case ToggleFlag(location: Location)

  def newGame(width: Int, height: Int, countMines: Int): Game.New =
    New(width, height, countMines)

  def actionFirst(game: Game.New, action: Open): Game =
    Game.first(game, action.location)

  def action(game: Game.Running, action: Action): Game =
    import Action.*

    action match
      case Open(l) =>
        Game.test(game, l) match
          case MineField => Game.lost(game, l)
          case w: WaterField =>
            val x = Game.opened(game, w)
            if x.board.fieldCount - x.mines.size == game.opened.size then
              Game.won(game)
            else
              x
      case ToggleFlag(l) => Game.flagged(game, l)
}
