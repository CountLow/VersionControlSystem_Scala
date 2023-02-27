package VersionControlSystem

import VersionControlSystem.VCS.vcs

import java.io.{BufferedReader, File, FileInputStream, FileWriter}
import scala.collection.mutable.Stack

/*
  Class containing the main functionality of the Version Control System.
  The parameter 'sourcePath' specifies the root of the repository.
  There are three attributes
      versionHistory is storing the last version
      currentCommit is the latest commit from the versionHistory
      stagingArea is a Set representing the stagingArea
*/
class VCS(val sourcePath : String):
  private var versionHistory : VersionHistory = VersionHistory.loadVersionHistory(sourcePath + "/.vcss")
  private var currentCommit : Commit = if (versionHistory != null) versionHistory.currentCommit else null
  private val stagingArea : collection.mutable.Set[String] = collection.mutable.Set()

  val ignoreFilter : IgnoreFilter = IgnoreFilter.generateFromFile(sourcePath)

  /*
    Creates the folder structure
  */
  def initializeVCS() : Unit =
  {
    new File(sourcePath + "/.vcss").mkdir()

    // Create version history if doesn't already exist
    val versionHistoryFile : File = new File(sourcePath + "/.vcss/versionHistory")
    if(versionHistoryFile.exists())
    {
      println("\nRepository already initialized!\n")
      return
    }
    else
    {
      new File(sourcePath + "/.vcss/versionHistory").createNewFile()
      val path : String = sourcePath + "/.vcss"
      versionHistory = VersionHistory(path)

    }

    // Create commit directory
    new File(sourcePath + "/.vcss/commits").mkdir()
    // Create first base commit so commit != null
    val commit: Commit = Commit(Array(), StructureDiff(sourcePath, null), null)
    commit.isHead = true
    Commit.saveToFile(commit, sourcePath + "/.vcss/commits/", commit.identifier)
    // save first commit in version History
    versionHistory.commitChanges(commit)
    VersionHistory.saveVersionHistory(versionHistory, sourcePath + "/.vcss")

    println("\nInitialized vcss repository.\n")

    // Create .vcssIgnore
    val vcssIgnore : File = new File(sourcePath + "/.vcss/.vcssIgnore")
    val fileWriter : FileWriter = FileWriter(vcssIgnore)
    fileWriter.write("path : .vcss\npath : VersionControlSystem\npath : VCS_S$.class\npath : VCS_S.class\npath : VCS_S.tasty")
    fileWriter.close()
  }

  /*
    prints the structural differences in the working copy
    e.g. when a file or directory was added
  */
  def status() : Unit =
  {
    val files: collection.mutable.Set[String] = collection.mutable.Set()

    val unsearched: Stack[File] = Stack(new File(sourcePath))

    while (unsearched.nonEmpty) {
      if (unsearched.top.isFile && !ignoreFilter.shouldIgnore(unsearched.top.getAbsolutePath))
        files.add(unsearched.pop.getPath)
      else if (unsearched.top.isDirectory && !ignoreFilter.shouldIgnore(unsearched.top.getAbsolutePath)) {
        unsearched.pushAll(unsearched.pop.listFiles())
      }
      else {
        unsearched.pop()
      }
    }

    if(currentCommit.isHead)
    {
      println("\nNot commited anything yet\n")


      val dummyStructureDiff : StructureDiff = StructureDiff(sourcePath, null)
      val structureDiff: StructureDiff = StructureDiff.generateDiff(dummyStructureDiff)

      println("Added and deleted files:")
      print(structureDiff.getString())
      println("")
      println("Changes in files:")

      for(path <- files)
      {
        val dummyDiff : FileDiff = FileDiff(path, null)
        val newDiff : FileDiff = FileDiff(path, dummyDiff)
        newDiff.generateDiff()
        if(!newDiff.getChanges().isEmpty) {
          println("\n\tFile: " + path + ":")
          newDiff.getChanges().map((i, o, c) => println("\t\t" + o.toString + ": '" + c + "' at line " + i))
        }
      }
    }
    else {
      println("\nCurrent commit: " + currentCommit.identifier + "\n")
      val structureDiff: StructureDiff = StructureDiff.generateDiff(currentCommit.structureDiff)
      StructureDiff.generateDiff(currentCommit.structureDiff)

      println("Added and deleted files:")
      println(structureDiff.getString())
      println("")
      println("Changes in files:")

      for(path <- files)
      {
        val newDiff : FileDiff = FileDiff(path, currentCommit.getDiffForFile(path))
        newDiff.generateDiff()
        if(!newDiff.getChanges().isEmpty) {
          println("\n\tFile: " + path + ":")
          newDiff.getChanges().map((i, o, c) => println("\t\t" + o.toString + ": '" + c + "' at line " + i))
        }
      }
    }

    println("")
  }

  /*
    Adds all changed files which are either directly specified or within a specified directory to the
    staging area.
  */
  def stage(paths : Array[String]) : Unit =
  {
    println("")

    for
      path <- paths
    do
      val data = new File(path)

      if(data.exists())
      {
        if(data.isDirectory) // Add all files in directory to staging area
        {
          val unsearched: Stack[File] = Stack(data)
//          unsearched.pushAll(data.listFiles())

          while (unsearched.nonEmpty) {
            if (unsearched.top.isFile && !ignoreFilter.shouldIgnore(unsearched.top.getAbsolutePath))
              stagingArea.add(unsearched.pop.getPath)
            else if (unsearched.top.isDirectory && !ignoreFilter.shouldIgnore(unsearched.top.getAbsolutePath)) {
              stagingArea.add(unsearched.top.getPath)
              unsearched.pushAll(unsearched.pop.listFiles())
            }
            else {
              println("\tIgnored: " + unsearched.top.getPath)
              unsearched.pop()
            }
          }
        }
        else if(data.isFile && !ignoreFilter.shouldIgnore(data.getAbsolutePath))
        {
          stagingArea.add(data.getPath)
        }

      }

    println("\nStaged the following files for commit:")
    stagingArea.map(f => println("\t" + f))

    saveStagingArea()
    println("")
  }

  /*
    save changes of current staging area as a commit
  */
  def commitChanges() =
  {
    loadStagingArea()
//    println(stagingArea)

    val commitDirectory : String = sourcePath + "/.vcss/commits/"
    var fileDiffs : Array[FileDiff] = Array()

    for(path <- stagingArea)
    {
      val filePath : String = sourcePath + "\\" + path

      if(new File(filePath).exists() && new File(filePath).isFile)
      {
        val fileDiff: FileDiff = FileDiff(filePath, if (currentCommit != null) currentCommit.getDiffForFile(path) else null)
        fileDiff.generateDiff()
//        println("Changes in " + filePath)
//        println(fileDiff.getChanges())
        fileDiffs = fileDiffs :+ fileDiff
      }
    }

    val dummyStructureDiff : StructureDiff = StructureDiff(sourcePath, null)
    val structureDiff : StructureDiff = StructureDiff.generateDiff(if(currentCommit != null)
                                                                   currentCommit.structureDiff else dummyStructureDiff)

    val commit : Commit = Commit(fileDiffs, structureDiff, currentCommit)
    Commit.saveToFile(commit, commitDirectory, commit.identifier)

    currentCommit = commit
    println("\nCreated new commit with identifier: " + commit.identifier + "\n")
    versionHistory.commitChanges(commit)
    VersionHistory.saveVersionHistory(versionHistory, sourcePath + "/.vcss")

    clearStagingArea()
  }

  /*
    Loads the data of a previous commit
  */
  def checkoutVersion(commitName : String) =
  {
    // Maybe implement warning



    // Load commit
    val commit : Commit = versionHistory.getCommitById(commitName)
    println("\nChecking out commit: " + commit.identifier + "\n")

    // Delete and add all changed files
    val structureDiff : StructureDiff = StructureDiff.generateDiff(commit.structureDiff)
//    println("Deleted directories: " + structureDiff.deletedDirectories)
//    println("Deleted files: " + structureDiff.deletedFiles)

    for (dD <- structureDiff.deletedDirectories)
      new File(dD).mkdir()

    for (dF <- structureDiff.deletedFiles)
      new File(dF).createNewFile()

    for (aF <- structureDiff.addedFiles)
      new File(aF).delete()

    for (aD <- structureDiff.addedDirectories)
      new File(aD).delete()


    for(fileDiff <- commit.fileDiffs)
    {
      val file : File = new File(fileDiff.sourcePath)
//      println(fileDiff.sourcePath)
//      println(fileDiff.getChanges())

      if(file.exists())
      {
        val content : String = fileDiff.generateVersion(fileDiff).toString
        val fileWriter : FileWriter = FileWriter(file)
        fileWriter.write(content)
        fileWriter.close()
      }
    }

  }

  

  /*
    Saves the staging area as a text document in the vcss directory
  */
  def saveStagingArea() : Unit =
  {
    val file : File = new File(sourcePath + "/.vcss/stagingArea.txt")
    val fileWriter : FileWriter = FileWriter(file)
    fileWriter.write(stagingArea.mkString("\n"))
    fileWriter.close()
  }

  /*
    generate the staging area from the text file
  */
  def loadStagingArea() : Unit =
  {
    stagingArea.clear()
    val bufferedReader : BufferedReader = io.Source.fromFile(sourcePath + "/.vcss/stagingArea.txt").bufferedReader()
    var input: String = bufferedReader.readLine()

    while(input != null)
    {
      stagingArea.add(input)
      input = bufferedReader.readLine()
    }
  }

  /*
    clears the staging area by creating a new empty text file
  */
  def clearStagingArea() : Unit =
  {
    val file: File = new File(sourcePath + "/.vcss/stagingArea.txt")
    val fileWriter: FileWriter = FileWriter(file)
    fileWriter.write("")
    fileWriter.close()
  }

  /*
  DEBUG FEATURE
  */
  def testFeature(args : Array[String]) =
  {
    val commit : Commit = Commit.loadFromFile(sourcePath + "/.vcss/commits/", currentCommit.identifier)
    println(commit)
  }

/*
  called by the main function
*/
object VCS:
  var vcs : VCS = null
  def createVCS(sourcePath : String) : VCS =
  {
    vcs = VCS(sourcePath)
    return vcs
  }