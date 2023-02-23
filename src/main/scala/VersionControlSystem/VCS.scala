package VersionControlSystem

import java.io.{BufferedReader, File, FileInputStream, FileWriter}
import scala.collection.mutable.Stack

/*
  Class containing the main functionality of the Version Control System.
  The parameter 'sourcePath' specifies the root of the repository.
*/
class VCS(val sourcePath : String):
  private val versionHistory : List[Commit] = loadVersionHistory()
  private var latestCommit : Commit = loadLatestCommit()
  private val stagingArea : collection.mutable.Set[String] = collection.mutable.Set()

  /*

  */
  private def loadVersionHistory() =
  {
    val path : String = sourcePath + "/.vcss/versionHistory"
//    val fIS : FileInputStream = FileInputStream(file.)
    null
  }

  /*

  */
  private def loadLatestCommit() : Commit =
  {
null
  }

  /*
    Creates the folder structure
  */
  def initializeVCS() : Unit =
  {
    // Create version history if doesn't already exist
    val versionHistoryFile : File = new File(sourcePath + "/.vcss/versionHistory")
    if(versionHistoryFile.exists())
    {
      println("Repository already initialized!")
      return
    }

    new File(sourcePath + "/.vcss").mkdir()
    versionHistoryFile.createNewFile()
    val fileWriter : FileWriter = new FileWriter(sourcePath + "/.vcss/versionHistory")
    fileWriter.write("Base commit #000000")
    fileWriter.close()

    // Create commit directory
    new File(sourcePath + "/.vcss/commits").mkdirs()

    println("Initialized vcss repository.")
  }

  /*

  */
  def status() : Unit =
  {
    if(latestCommit == null)
    {
//      val fileDiffs: List[FileDiff] = latestCommit.fileDiffs
      val structureDiff: StructureDiff = StructureDiff(sourcePath, null)
      structureDiff.generateDiff()

      print(structureDiff.getString())
    }
    else {
      val fileDiffs: Array[FileDiff] = latestCommit.fileDiffs
      val structureDiff: StructureDiff = StructureDiff(sourcePath, latestCommit.structureDiff)
      structureDiff.generateDiff()

      print(structureDiff.getString())
    }
  }

  /*
    Adds all changed files which are either directly specified or within a specified directory to the
    staging area.
  */
  def stage(paths : Array[String]) : Unit =
  {
    for
      path <- paths
    do
      val data = new File(path)

      if(data.exists())
      {
        if(data.isDirectory) // Add all files in directory to staging area
        {
          val unsearched: Stack[File] = Stack(data)
          while (unsearched.nonEmpty) {
            if (unsearched.top.isFile)
              stagingArea.add(unsearched.pop.getPath)
            else if (unsearched.top.isDirectory) {
              unsearched.pushAll(unsearched.pop.listFiles())
            }
            else
              unsearched.pop()
          }
        }
        else if(data.isFile)
        {
          stagingArea.add(data.getPath)
        }
      }

      println("Staged the following files for commit:")
      println(stagingArea)
  }

  /*

  */
  def commitChanges() =
  {
    val commitDirectory : String = sourcePath + "/.vcss/commits/"
    var fileDiffs : Array[FileDiff] = Array()

    for(path <- stagingArea) {
      val fileDiff : FileDiff = FileDiff(path, if(latestCommit != null )latestCommit.getDiffForFile(path) else null)
      fileDiff.generateDiff()
      fileDiffs = fileDiffs :+ fileDiff

      println(fileDiff.getString())
    }

    val structureDiff : StructureDiff = StructureDiff(sourcePath, if(latestCommit != null )latestCommit.structureDiff else null)
    structureDiff.generateDiff()

    val commit : Commit = Commit(fileDiffs, structureDiff, latestCommit)
    CommitHandler.saveToFile(commit, commitDirectory, "FirstCommit")
  }

  /*
  DEBUG FEATURE
  */
  def testFeature(args : Array[String]) =
  {
    val commit : Commit = CommitHandler.loadFromFile(sourcePath + "/.vcss/commits/", "FirstCommit")
    println(commit)
  }