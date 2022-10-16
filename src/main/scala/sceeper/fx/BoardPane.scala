package sceeper.fx

import scalafx.Includes.*
import scalafx.scene.input.MouseButton.Secondary
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.GridPane
import sceeper.*

import scala.collection.mutable

/**
 * Generates the board with the given dimensions and provides an lookup mechanism of [[WaterTile]]s.
 * @param width how many tiles wide should the board be
 * @param height how many tiles high should the board be
 * @param mainClick the action triggered with a left-mouse-click
 * @param secondaryClick the triggered with right-mouse-click
 */
class BoardPane(width: Int, height: Int, mainClick: (WaterTile, BoardPane) => Unit, secondaryClick: (WaterTile) => Unit ) extends GridPane {

  styleClass.add("board")

  private val tiles = new mutable.HashMap[Location, WaterTile](width * height, 1.0)
  private val ref = this

  for x <- 0 until width
      y <- 0 until height
  do
    val l = Location(x, y)
    val b = new WaterTile(l) {
      onAction = _ => {
        mainClick.apply(tiles(location), ref)
      }
      onMouseClicked = (me: MouseEvent) => me.button match
        case Secondary => secondaryClick.apply(tiles(location))
        case _ =>

    }
    tiles.addOne(l -> b)
    add(b, x, y)


  /**
   * A lookup from [[Location]] to the backing [[WaterTile]]
   * @param location coordinates of the [[WaterTile]] to be returned
   * @return
   */
  def lookup(location: Location): WaterTile =
    tiles(location)

  def showMines(mines: Set[Location]): Unit =
    mines.foreach(lookup(_).mine())
}
