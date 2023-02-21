package VersionControlSystem

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

/*
  Class that represents the changes between two versions.
  Each files change is saved as a corresponding instance of 'Diff'.
*/
class Commit extends Serializable:
  private val directoryPath : String = ""
  private val diffs : List[Diff] = List()

  def applyCommit() : Unit =
  {

  }

  def applyDiff() : Unit =
  {

  }

  def saveToFile(sourcePath : String, name : String) : Unit =
  {
        val path = sourcePath + "/" + name + ".commit"
        val fOS : FileOutputStream = new FileOutputStream(path)
        val oOS : ObjectOutputStream = new ObjectOutputStream(fOS)
        oOS.writeObject(this)
        oOS.close()
  }

  def loadFromFile(sourcePath : String, name : String) : Commit =
  {
        val path = sourcePath + "/" + name + ".commit"
        val fIS : FileInputStream = new FileInputStream(path)
        val oIS : ObjectInputStream = new ObjectInputStream(fIS)
        val data : Commit = oIS.readObject().asInstanceOf[Commit]
        oIS.close()

        data
  }


