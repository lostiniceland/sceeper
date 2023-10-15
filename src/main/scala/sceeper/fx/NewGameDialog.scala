package sceeper.fx

import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{ButtonType, ChoiceBox, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.collections.ObservableBuffer
import sceeper.fx.Dimension
import sceeper.fx.Level
import sceeper.fx.NewGameDialog.Result

class NewGameDialog extends Dialog[Result] {

  title = "New Game"
  headerText = "Specify the size and difficulty"

  val okButton = new ButtonType("OK", ButtonData.OKDone)
  dialogPane().getButtonTypes.addAll(okButton, ButtonType.Cancel)


  private val dimension = new ChoiceBox[Dimension]{
    items = ObservableBuffer.from(Dimension.values)
  }
  dimension.getSelectionModel.clearAndSelect(1)
  private val level = new ChoiceBox[Level]{
    items = ObservableBuffer.from(Level.values)
  }
  level.getSelectionModel.clearAndSelect(0)


  private val grid = new GridPane() {
    hgap = 10
    vgap = 10
    padding = Insets(20, 100, 10, 10)

    add(new Label("Dimension:"), 0, 0)
    add(dimension, 1, 0)
    add(new Label("Level:"), 0, 1)
    add(level, 1, 1)
  }

  dialogPane().setContent(grid)

  resultConverter = dialogButton =>
    if dialogButton == okButton then
      Result(dimension.getValue, level.getValue)
    else
      null
}

object NewGameDialog {

  case class Result(dimension: Dimension, level: Level)

}
