package sceeper

import sceeper.*

import scala.annotation.tailrec
import scala.util.Random



/**
 * The Board is responsible for the playing field. It knows if a Location has a valid coordinate
 * and what are the valid neighbors of a given Location.
 * @param width the Boards width. Must be > 1.
 * @param height the Boards height. Must be > 1.
 */
class Board private[sceeper] (val width: Int, val height: Int):
  require(width > 1)
  require(height > 1)

  private val topLeft = Location(0, height - 1)
  private val bottomLeft = Location(0, 0)
  private val topRight = Location(width - 1, height - 1)
  private val bottomRight = Location(width - 1, 0)

  def fieldCount: Int = width * height
  
//  def hasBeenCleared(opened: Set[WaterField]): Boolean =
//    fieldCount - _mines.size == opened.size

  /**
   * Tests, if the given location is within the Boards bounds
   * @param l the location to validate
   * @return true, when locations x and y are within bounds, otherwise false
   */
  def isValid(l: Location): Boolean = l.x >= 0 && l.x < width && l.y >= 0 && l.y < height


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
