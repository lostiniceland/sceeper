package sceeper.fx

import javafx.css.PseudoClass
import javafx.scene.image.ImageView
import scalafx.Includes.*
import scalafx.scene.control.ToggleButton
import sceeper.Location

private [fx] class WaterTile(val location: Location) extends ToggleButton {

  private var flagged = false

  private val minePseudo = PseudoClass.getPseudoClass("mine")
  private val flaggedPseudo = PseudoClass.getPseudoClass("flagged")
  private val emptyPseudo = PseudoClass.getPseudoClass("empty")
  private val correctPseudo = PseudoClass.getPseudoClass("correct")
  private val wrongPseudo = PseudoClass.getPseudoClass("wrong")
  styleClass.add("water")

  private[fx] def flag(): Unit =
    flagged = true
    this.pseudoClassStateChanged(flaggedPseudo, flagged)

  private[fx] def unflag(): Unit =
    flagged = false
    this.pseudoClassStateChanged(flaggedPseudo, flagged)

  private[fx] def opened(proximityMines: Int): Unit =
    selected = true
    style = proximityMines match {
      case p if p >= 3 => "-fx-text-fill: red"
      case 2 => "-fx-text-fill: yellow"
      case 0|1 => "-fx-text-fill: blue"
    }
    if proximityMines != 0 then
      text = proximityMines.toString
    else
      this.pseudoClassStateChanged(emptyPseudo, true)
    disarm()

  /**
   * Displays this tile as a mine
   */
  private[fx] def mine(): Unit =
    if (flagged)
      this.pseudoClassStateChanged(correctPseudo, true)
    this.pseudoClassStateChanged(minePseudo, true)

  /**
   * Displays this tile as the mine that failed the game
   */
  private[fx] def triggeredMine(): Unit =
    this.pseudoClassStateChanged(minePseudo, true)
    this.pseudoClassStateChanged(wrongPseudo, true)
}
