package sceeper

/**
 * A Location is a Coordinate, which only allows positive values, and represents a position on the playing field.
 * @param x Locations x coordinate
 * @param y Locations y coordinate
 */
case class Location(x: Int, y: Int):
  require(x >= 0)
  require(y >= 0)
end Location