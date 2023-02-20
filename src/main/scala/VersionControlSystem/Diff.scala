package VersionControlSystem

import java.io.{BufferedReader, File, FileInputStream, FileOutputStream, FileWriter, ObjectInputStream, ObjectOutputStream}

/*
  Class representing the change of a single file between two versions.
*/
class Diff extends Serializable:
  private var changes : List[(Int, Operation, String)] = List()


  /*
    Saves a diff instances in a file with format:
    index.:operation.<CONTENT_BEGIN>content<CONTENT_END>
  */
  def saveAsFile(sourcePath : String, name : String) : Unit =
  {
    val path = sourcePath + "/" + name + ".diff"
    val fOS : FileOutputStream = new FileOutputStream(path)
    val oOS : ObjectOutputStream = new ObjectOutputStream(fOS)
    oOS.writeObject(changes)
    oOS.close()
  }

  def loadFromFile(sourcePath : String, name : String) : Unit =
  {
    val path = sourcePath + "/" + name + ".diff"
    val fIS : FileInputStream = new FileInputStream(path)
    val oIS : ObjectInputStream = new ObjectInputStream(fIS)
    oIS.close()
  }

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