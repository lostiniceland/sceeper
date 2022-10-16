package sceeper.fx

import javafx.stage.StageStyle
import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.*
import scalafx.scene.input.MouseButton.Secondary
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.scene.image.Image
import sceeper.*
import sceeper.fx.WaterTile

import java.lang.Thread.UncaughtExceptionHandler
import java.nio.file.Files
import scala.util.{Failure, Success}
import sceeper.Action.*
import sceeper.ActionResult.*

enum Level:
  case Easy
  case Normal
  case Hard


enum Dimension:
  case Small
  case Normal
  case Big
  case Huge


object SceeperFX extends JFXApp3 with UncaughtExceptionHandler {

  Thread.setDefaultUncaughtExceptionHandler(this)

  private var game = createGame(Dimension.Normal, Level.Normal)

  private val actionOpen = (tile: WaterTile, boardPane: BoardPane) => game.execute(Action.Open(tile.location)) match
    case ActionResult.Opened(openedFields) =>
      openedFields.foreach( o => boardPane.lookup(o.location).opened(o.proximityMines))
    case ActionResult.GameOver(mines) =>
      mines.foreach(m => boardPane.lookup(m).mine())
      boardPane.lookup(tile.location).triggeredMine()
    case ActionResult.Victory(mines) =>
      boardPane.showMines(mines)
      boardPane.disable = true
    case _ =>

  private val actionFlag = (tile: WaterTile) => game.execute(ToggleFlag(tile.location)) match
    case ActionResult.Flagged => tile.flag()
    case ActionResult.UnFlagged => tile.unflag()

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage {
      title.value = "Sceeper"
      icons.add(new Image("icon.png"))
      resizable = false
      scene = new Scene {
        stylesheets.add("styles/default/style.css")
        root = new BorderPane {
          top = new MenuBar {
            val gameMenu: Menu = new Menu("Game") {
              val itemNew: MenuItem = new MenuItem("New...") {
                onAction = _ =>
                  val dialog = new NewGameDialog()
                  val result = dialog.showAndWait()
                  import NewGameDialog.Result
                  result match
                    case Some(Result(dimension,level)) =>
                      game = createGame(dimension, level)
                      bottom = new BoardPane(game.board.width, game.board.height, actionOpen, actionFlag )
                    case _ =>
              }
              val itemSolve: MenuItem = new MenuItem("Solve") {
                onAction = _ => println("TODO solve")
              }
              val itemQuit: MenuItem = new MenuItem("Quit") {
                onAction = _ => Platform.exit()
              }
              items = List(itemNew, itemSolve, itemQuit)
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


  def createGame(dimension: Dimension, level: Level) =
    val size = dimension match
      case Dimension.Small => (5, 5)
      case Dimension.Normal => (10, 10)
      case Dimension.Big => (20, 20)
      case Dimension.Huge => (40, 20)

    val fieldCount = size._1 * size._2

    val minesCount = level match
      case Level.Easy => fieldCount * 0.1
      case Level.Normal => fieldCount * 0.2
      case Level.Hard => fieldCount * 0.5

    Sceeper(size._1, size._2, minesCount.toInt)
}


