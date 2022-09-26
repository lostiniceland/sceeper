package sceeper.fx

import scalafx.Includes.*
import scalafx.scene.control.ToggleButton
import sceeper.Location

private [fx] class WaterTile(val location: Location) extends ToggleButton {

  private[fx] def flag(): Unit =
    style = "-fx-background-color: yellow"

  private[fx] def unflag(): Unit =
    style = ""

  private[fx] def opened(proximityMines: Int): Unit =
    selected = true
    style = proximityMines match {
      case p if p >= 3 => "-fx-text-fill: red"
      case 2 => "-fx-text-fill: yellow"
      case 0|1 => "-fx-text-fill: blue"
    }
    text = proximityMines.toString
    disarm()

  private[fx] def mine(): Unit =
    style = "-fx-background-color: red"
}
