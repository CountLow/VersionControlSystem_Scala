package VersionControlSystem

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

/*
  Operations on Commits
*/
object CommitHandler {
  def saveToFile(commit : Commit, sourcePath: String, name: String): Unit = {
    val path = sourcePath + name + ".commit"
    val fOS: FileOutputStream = new FileOutputStream(path)
    val oOS: ObjectOutputStream = new ObjectOutputStream(fOS)
    oOS.writeObject(commit)
    oOS.close()
  }

  def loadFromFile(sourcePath: String, name: String): Commit = {
    val path = sourcePath + "/" + name + ".commit"
    val fIS: FileInputStream = new FileInputStream(path)
    val oIS: ObjectInputStream = new ObjectInputStream(fIS)
    val data: Commit = oIS.readObject().asInstanceOf[Commit]
    oIS.close()

    data
  }
}
