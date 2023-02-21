package VersionControlSystem

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

/*
  Class representing the change of a single file between two versions.
*/
class Diff:
  private var changes : List[(Int, Operation, String)] = List()

  def addChange(index : Int, operation : Operation, content : String) : Unit =
  {
    changes = changes :+ (index, operation, content)
  }

  def addChange(index: Int, operation: Operation, content: List[String]): Unit = {
    for(c <- content)
      changes = changes :+ (index, operation, c)
  }

  def getString() : String =
  {
    return changes.mkString("\n")
  }

  def getChanges() : List[(Int, Operation, String)] =
  {
    return changes
  }