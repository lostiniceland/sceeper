package sceeper.fx

import javafx.stage.StageStyle
import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.Image
import scalafx.scene.input.MouseButton.Secondary
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, GridPane}
import sceeper.Sceeper
import sceeper.Sceeper.Action.*
import sceeper.Game
import sceeper.Sceeper.Action
import sceeper.fx.WaterTile

import java.lang.Thread.UncaughtExceptionHandler
import java.nio.file.Files
import scala.util.{Failure, Success}

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

  private var game: Game = createGame(Dimension.Normal, Level.Normal)

  private def updateState(updated: Game, f: () => Unit): Unit =
    this.game = updated
    f()


  private val actionOpen = (tile: WaterTile, boardPane: BoardPane) =>

    val r = game match
      case g: Game.New => Sceeper.actionFirst(g, Action.Open(tile.location))
      case g: Game.Running => Sceeper.action(g, Action.Open(tile.location))

    r match
      case g: Game.Running => updateState(g, () => g.opened.foreach(o => boardPane.lookup(o.location).opened(o.proximityMines)))
      case g: Game.Lost => updateState(g, () => {
        g.otherMines.foreach(m => boardPane.lookup(m).mine())
        boardPane.lookup(g.mine).triggeredMine()
        boardPane.disable = true
      })
      case g: Game.Won => updateState(g, () => {
        boardPane.showMines(g.mines)
        boardPane.disable = true
      })
      case _ =>


  private val actionFlag = (tile: WaterTile) =>

    val g = game match
      case g: Game.Running => Sceeper.action(g, ToggleFlag(tile.location))
      case _ => game
    updateState(g, () => {
      g match
        case g: Game.Running =>
          if g.flagged.contains(tile.location) then
            tile.flag()
          else
            tile.unflag()
        case _ => ()
      })

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage {
      title.value = "Sceeper"
      icons.add(new Image("icon.png"))
      // resizable needs to be enabled in order to have different field sizes
//      resizable = false
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
                      val newgame = createGame(dimension, level)
                      updateState(newgame, () => {
                        bottom = new BoardPane(newgame.width, newgame.height, actionOpen, actionFlag )
                        stage.sizeToScene()
                      })
                    case _ =>
              }
              val itemSolve: MenuItem = new MenuItem("Solve") {
                onAction = _ =>
                  println("foo")
                  // Trigger Solver
//                  Solver(game).solve()
                  // Update UI
              }
              val itemQuit: MenuItem = new MenuItem("Quit") {
                onAction = _ => Platform.exit()
              }
              items = List(itemNew, itemSolve, itemQuit)
            }
            menus = List(gameMenu)
          }
          bottom = new BoardPane(game.asInstanceOf[Game.New].width, game.asInstanceOf[Game.New].height, actionOpen, actionFlag )
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


  private def createGame(dimension: Dimension, level: Level): Game.New =
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

    Sceeper.newGame(size._1, size._2, minesCount.toInt)
}


