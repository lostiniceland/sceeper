package sceeper

import sceeper.Board.randomMines

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
class Board private[sceeper] (val width: Int, val height: Int, private[sceeper] val mineGenerator: Location => Set[Location]):
  require(width > 1)
  require(height > 1)

//  require(mines.nonEmpty)
//  require(mines.size < fieldCount)
//  require(mines.forall(l => isValid(l)))
  private var initialized = false
  private var _mines = Set.empty[Location]
  private val topLeft = Location(0, height - 1)
  private val bottomLeft = Location(0, 0)
  private val topRight = Location(width - 1, height - 1)
  private val bottomRight = Location(width - 1, 0)

  def mines: Set[Location] = _mines

  def fieldCount: Int = width * height
  
  def hasBeenCleared(opened: Set[WaterField]): Boolean =
    fieldCount - _mines.size == opened.size

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

    if !initialized then
      _mines = mineGenerator(l)
      initialized = true

    if _mines.contains(l) then
      MineField
    else
      WaterField(neighborsOf(l).count(l => _mines.contains(l)), l)


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
    Location.of(x,y).filter(isValid)
end Board


object Board:

  /**
   * Creates a new Board with the given bounds and populates it with as many random mines as given by count. 
   */
  def apply(width: Int, height: Int, countMines: Int): Board =
    new Board(width, height, randomMines(countMines, width, height))

  private val randomGenerator = Random()

  /**
   * Generates a set of mines.
   * Implementation uses more memory by populating an array with all possible fields and selecting randomly from the
   * array. Another solution would be to generate completely random but this would need to filter out duplicates which
   * can be time-consuming when having many mines (due to conflicts in the randomness)
   * @param count amount of mines
   * @param maxX width boundary
   * @param maxY height boundary
   * @param first the first location which has already been opened (and should not be a mine)
   * @return
   */
  private def randomMines(count: Int, maxX: Int, maxY: Int): Location => Set[Location] =
    def getRandomElement(seq: collection.mutable.Buffer[Location]): Location =
      val element = seq(randomGenerator.nextInt(seq.length))
      seq -= element
      element

    val allMines =
      (for
        x <- 0 until maxX
        y <- 0 until maxY
      yield Location(x, y)).toBuffer

    (first: Location) => (1 to count).map(_ => getRandomElement(allMines.-=(first))).toSet

end Board


