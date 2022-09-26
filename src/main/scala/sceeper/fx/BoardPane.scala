package sceeper.fx

import scalafx.Includes.*
import scalafx.scene.input.MouseButton.Secondary
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.GridPane
import sceeper.*

import scala.collection.mutable

class BoardPane(width: Int, height: Int, mainClick: (WaterTile, BoardPane) => Unit, secondaryClick: (WaterTile) => Unit ) extends GridPane {

  val tiles = new mutable.HashMap[Location, WaterTile](width * height, 1.0)
  val ref = this

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


  def lookup(location: Location): WaterTile =
    tiles(location)
}
