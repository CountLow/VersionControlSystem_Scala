package VersionControlSystem

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream, File}

/*
  Contains information about branches
*/
class VersionHistory extends Serializable:
  var currentCommit : Commit = null
  private var commits : Array[Commit] = Array()

  def commitChanges(commit : Commit) = {
    currentCommit = commit
  }


object VersionHistory:
  def loadVersionHistory(vcssPath : String) : VersionHistory = {
    if(!new File(vcssPath + "vH.versionHistory").exists())
      return null

    val fIS : FileInputStream = FileInputStream(vcssPath + "vH.versionHistory")
    val oIS : ObjectInputStream = ObjectInputStream(fIS)
    oIS.readObject.asInstanceOf[VersionHistory]
  }

  def saveVersionHistory(versionHistory : VersionHistory, vcssPath : String) : Unit =
  {
    val fOS : FileOutputStream = FileOutputStream(vcssPath + "vH.versionHistory")
    val oOS : ObjectOutputStream = ObjectOutputStream(fOS)
    oOS.writeObject(versionHistory)
    println(versionHistory)
  }



