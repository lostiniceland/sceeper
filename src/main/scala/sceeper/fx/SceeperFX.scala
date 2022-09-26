package sceeper.fx

import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.*
import scalafx.scene.input.MouseButton.Secondary
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, GridPane}
import sceeper.*
import sceeper.fx.WaterTile

import java.lang.Thread.UncaughtExceptionHandler
import java.nio.file.Files
import scala.util.{Failure, Success}
import sceeper.Action.*
import sceeper.ActionResult.*

object SceeperFX extends JFXApp3 with UncaughtExceptionHandler {

  Thread.setDefaultUncaughtExceptionHandler(this)

  val game = Sceeper(10, 10, 10)

  val actionOpen = (tile: WaterTile, boardPane: BoardPane) => game.execute(Action.Open(tile.location)) match
    case ActionResult.Opened(openedFields) =>
      println(openedFields)
      openedFields.foreach( o => boardPane.lookup(o.location).opened(o.proximityMines))
    case ActionResult.GameOver(mines) =>
      println(s"BOOOM: $mines")
      mines.foreach(m => boardPane.lookup(m).mine())
    case _ =>

  val actionFlag = (tile: WaterTile) => game.execute(ToggleFlag(tile.location)) match
    case ActionResult.Flagged => tile.flag()
    case ActionResult.UnFlagged => tile.unflag()

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage {
      title.value = "SweeperFX"
      width = 600
      height = 400

      scene = new Scene {
        root = new BorderPane {
          top = new MenuBar {
            val gameMenu = new Menu("Game") {
              val itemNew = new MenuItem("New...") {
                onAction = _ => println("TODO new game wizard")
              }
              val itemQuit = new MenuItem("Quit") {
                onAction = _ => Platform.exit()
              }
              items = List(itemNew, itemQuit)
            }
            menus = List(gameMenu)
          }
          bottom = new BoardPane(game.board.width, game.board.height, actionOpen, actionFlag )
        }
      }
    }

  override def uncaughtException(t: Thread, e: Throwable): Unit =
    System.err.println("***Default exception handler***")
    if (Platform.isFxApplicationThread)
      e.printStackTrace()
      new Alert(AlertType.Error, e.getMessage, ButtonType.OK).showAndWait()
    else
      System.err.println("An unexpected error occurred in " + t)
    Platform.exit()

}


