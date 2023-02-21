package VersionControlSystem

import java.io.{BufferedReader, File, FileWriter}
import collection.mutable.Set
import collection.mutable.Stack


enum Operation:
  case Insertion
  case Deletion


/*
  Class containing the main functionality of the Version Control System.
  The parameter 'sourcePath' specifies the root of the repository.
*/
class VCS(val sourcePath : String):
  private var latestCommit : Commit = loadLatestCommit()
  private var stagingArea : List[FileDiff] = List()

  /*

  */
  private def loadLatestCommit() : Commit =
  {
    null
  }

  /*
    Finds the differences between two versions of the same file.
    The found differences are saved as instance of the 'Diff' class.

    INFO:
    A practical search length before aborting has to be determined.
  */
  def generateDiffForFile(pathA : String, pathB : String) : FileDiff = {
    val diff : FileDiff = FileDiff()
    val bufferedReaderA : BufferedReader =  io.Source.fromFile(pathA).bufferedReader()
    val bufferedReaderB : BufferedReader =  io.Source.fromFile(pathB).bufferedReader()
    var index : Int = 0
    var stringA : String = ""
    var stringB : String = ""

    while(stringA != null)
    {
      bufferedReaderB.mark(9999) // Practical limit should be tested (number of chars!!!)
      stringA = bufferedReaderA.readLine()
      stringB = bufferedReaderB.readLine()
      index = index + 1

      if(stringA != stringB) {
        var tempContent: List[String] = List()

        while (stringA != stringB && stringB != null && tempContent.length < 20) // Abort if temporary list exceeds certain size
        {
          tempContent = tempContent :+ stringB
          stringB = bufferedReaderB.readLine()
        }

        if (stringB == null && stringA != null) // Deletion or cutoff
        {
          diff.addChange(index, Operation.Deletion, stringA)
          bufferedReaderB.reset()
        }
        else // Insertion
        {
          diff.addChange(index, Operation.Insertion, tempContent)
        }
      }
    }

    diff
  }

  /*
    Applies all changes of a diff to a base file.
  */
  def applyDiffOnFile(path : String, diff : FileDiff) : Unit =
  {
    val bufferedReader : BufferedReader = io.Source.fromFile(path).bufferedReader()
    val stringBuffer : StringBuffer = new StringBuffer()
    
    var changes : List[(Int, Operation, String)] = diff.getChanges()
    var (index, operation, content) : (Int, Operation, String) = changes.head
    changes = changes.tail

    var lineNumber : Int = 0
    var lineContent : String = ""

    while(lineContent != null || lineNumber <= index)
    {
      // Add new content if at index and operation is insertion
      if(lineNumber == index && operation == Operation.Insertion)
        stringBuffer.append(content + "\n")

      // Add old content if not yet at index or operation is deletion (but not if first line)
      if((lineNumber != index || operation != Operation.Deletion) && lineNumber != 0 && lineContent != null)
        stringBuffer.append(lineContent + "\n")

      // Load next change
      if(lineNumber == index)
      {
        // Load next line
        lineNumber += 1
        lineContent = bufferedReader.readLine()

        index = if (changes.nonEmpty) changes.head._1 else -1
        operation = if (changes.nonEmpty) changes.head._2 else Operation.Deletion
        content = if (changes.nonEmpty) changes.head._3 else ""
        changes = if (changes.nonEmpty) changes.tail else changes
      }
      else
      {
        lineNumber += 1
        lineContent = bufferedReader.readLine()
      }
    }

    val fileWriter : FileWriter = new FileWriter(path)
    fileWriter.write(stringBuffer.toString)
    fileWriter.close()
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

        }
        else if(data.isFile)
        {
          // Check if file already exists in last commit or is new
          // Add NEW_FILE_DIFF or add diff of file content

          // If file already existed we have to apply all diffs on base version to find diff
        }
      }
  }

  /*

  */
  def commitChanges() =
  {
    /*if(latestCommit == null) // Create base commit
    {

    }
    else
    {

    }*/

    // DON'T do it like this!
    // Represent no commit as empty
    // => First commit then also represent only changes

    val commit : Commit = Commit()

  }
  /*
    Generates the difference between two folders/directories
    e.g. wether there are new files since the last commit

    Returns a List of Tupels with Filename and the according Diffs
  */
  def generateDiffForAllFiles(path : String, previousPath : String) : List[(String, FileDiff)] = {

    val dir : File = new File(path)
    val previousdir : File = new File(previousPath)
    var dict : Stack[File] = Stack(dir)
    var pdict : Stack[File] = Stack(previousdir)
    //List of all files in path directory and their names
    var files : List[File] = List()
    var filesname : List[String] = List()
    //List of all files in previous directory and their names
    var pfiles : List[File] = List()
    var pfilesname : List[String] = List()

    while(dict.nonEmpty) {

      if (dict.top.isFile) {
        val temp: File = dict.pop
        files = temp :: files
        filesname = temp.getName :: filesname
      }
      else if (dict.top.isDirectory) {
        dict.pushAll(dict.pop.listFiles())
      }
      else
        dict.pop()
    }
    while (pfiles.nonEmpty) {

      if (pdict.top.isFile) {
        val temp2 : File = pdict.pop
        pfiles = temp2 :: pfiles
        pfilesname = temp2.getName :: pfilesname
      }
      else if (pdict.top.isDirectory) {
        pdict.pushAll(pdict.pop.listFiles())
      }
      else
        pdict.pop()
    }
    // find common filenames, every other file was either added or deleted

    files = files.toList
    pfiles = pfiles.toList
    val common : List[String] =  for name <- filesname if pfilesname.contains(name) yield name

    var diff : List[(String, FileDiff)] = List()


    // every file in the current directory look for file in previous directory
    for
      x <- files
    do
      if (common.contains(x.getName())) {
        for
          y <- pfiles
        do
          if (x.getName == y.getName) {
            //save Path of x, so applyDiffOnFile can be called eventually
            diff = (x.getPath, generateDiffForFile(x.getPath, y.getPath)) :: diff
          }
      }

    diff
  }

  /*
  DEBUG FEATURE
  */
  def testFeature(args : Array[String]) =
  {
    val structureDiff : StructureDiff = StructureDiff()
    structureDiff.generateDiff(args(0))
  }