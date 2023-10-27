package sceeper

/**
 * A Location is a Coordinate, which only allows positive values, and represents a position on the playing field.
 * @param x Locations x coordinate (must be >= 0)
 * @param y Locations y coordinate (must be >= 0)
 */
case class Location(x: Int, y: Int):
  require(x >= 0 && y >= 0)
end Location

object Location:

  def of(x: Int, y: Int) : Option[Location] =
    if(x >= 0 && y >= 0)
      Some(Location(x,y))
    else
      None

end Location