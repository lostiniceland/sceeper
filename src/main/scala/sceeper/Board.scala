package sceeper

import scala.annotation.tailrec
import scala.util.Random


sealed trait Field
case class WaterField(proximityMines: Int, location: Location) extends Field
case object MineField extends Field

/**
 * The Board is responsible for the playing field. It knows if a Location has a valid coordinate,
 * if a field is a mine or not, and what are the valid neighbors of a given Location.
 * @param width the Boards width. Must be > 1.
 * @param height the Boards height. Must be > 1.
 * @param mines mines can be passed in for test-setup. Normally only passed by apply in the companion. Not null and all
 *              locations must be within the Boards bounds.
 */
class Board private[sceeper] (val width: Int, val height: Int, private[sceeper] val mines: Set[Location]):
  require(width > 1)
  require(height > 1)

  require(mines.nonEmpty)
  require(mines.size < fieldCount)
  require(mines.forall(l => isValid(l)))

  private val topLeft = Location(0, height - 1)
  private val bottomLeft = Location(0, 0)
  private val topRight = Location(width - 1, height - 1)
  private val bottomRight = Location(width - 1, 0)

  def fieldCount: Int = width * height
  
  def hasBeenCleared(opened: Set[WaterField]): Boolean =
    fieldCount - mines.size == opened.size

  /**
   * Tests, if the given location is within the Boards bounds
   * @param l the location to validate
   * @return true, when locations x and y are within bounds, otherwise false
   */
  def isValid(l: Location): Boolean = l.x >= 0 && l.x < width && l.y >= 0 && l.y < height


  /**
   * Checks if "at" the location is a [[MineField]] or [[WaterField]].
   * In case of a WaterField, the number of mines on neighboring fields is returned.
   * @param l the location to look at
   * @return MineField or WaterField
   */
  private[sceeper] def at(l: Location): Field =
    if mines.contains(l) then
      MineField
    else
      WaterField(neighborsOf(l).count(l => mines.contains(l)), l)


  private def isLeftEdge(l: Location): Boolean = l.x == 0
  private def isRightEdge(l: Location): Boolean = l.x == width - 1
  private def isBottomEdge(l: Location): Boolean = l.y == 0
  private def isTopEdge(l: Location): Boolean = l.y == height - 1

  def isEdge(l: Location): Boolean =
    !isCorner(l) && (isLeftEdge(l) || isRightEdge(l) || isTopEdge(l) || isBottomEdge(l))

  def isCorner(l: Location): Boolean =
    l == topLeft || l == bottomLeft || l == topRight || l == bottomRight

  /**
   * Returns all Locations that are neighbors of the given location within the Boards bounds
   * @param l the locations whose neighbors are to be returned
   * @return neighbors of location
   */
  def neighborsOf(l: Location): Set[Location] =
    val leftTop = locationOf(l.x - 1, l.y + 1)
    val left = locationOf(l.x - 1, l.y)
    val leftBottom = locationOf(l.x - 1, l.y - 1)
    val top = locationOf(l.x, l.y + 1)
    val bottom = locationOf(l.x, l.y - 1)
    val rightTop = locationOf(l.x + 1, l.y + 1)
    val right = locationOf(l.x + 1, l.y)
    val rightBottom = locationOf(l.x + 1, l.y - 1)
    
    Set(
      leftTop,
      left,
      leftBottom,
      top,
      bottom,
      rightTop,
      right,
      rightBottom).flatten

  /**
   * Creates a new Location and validates it against the current Board. If it is within the Boards bounds
   * Some(Location) is returned, otherwise None
   *
   * @param x     Locations x coordinate
   * @param y     Locations y coordinate
   * @return Some(Location) when valid, otherwise None
   */
  private[this] def locationOf(x: Int, y: Int): Option[Location] =
    try {
      val l = Location(x, y)
      Some(l).filter(isValid)
    }catch {
      _ => None
    }
end Board


object Board:

  /**
   * Creates a new Board with the given bounds and populates it with as many random mines as given by count. 
   */
  def apply(width: Int, height: Int, countMines: Int): Board =
    new Board(width, height, randomMines(countMines, width, height))

  private val randomGenerator = Random()

  private def nextRandomLocation(maximumX: Int, maximumY: Int): Location =
    Location(randomGenerator.between(0, maximumX), randomGenerator.between(0, maximumY))

  private def randomMines(count: Int, maxX: Int, maxY: Int): Set[Location] =
    @tailrec
    def replaceDuplicate(accumulator: Set[Location], element: Location): Set[Location] =
      if accumulator.contains(element) then
        // create a new Location, which again could be a dup, hence call this function again
        replaceDuplicate(accumulator, Board.nextRandomLocation(maxX, maxY))
      else
        accumulator + element

    List.fill(count) {
      Board.nextRandomLocation(maxX, maxY)
    }.foldLeft(Set[Location]()) { (partialResult, element) =>
      replaceDuplicate(partialResult, element)
    }

end Board
