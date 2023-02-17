package VersionControlSystem

/*
Saves the changes of one single file.
*/

class Diff:
  enum Operation:
    case Insertion
    case Deletion

  private val pathToFile : String = ""
  private var changes : List[(Int, Operation, String)] = List()

  def saveAsFile() =
  {

  }

  def loadFromFile() =
  {

  }