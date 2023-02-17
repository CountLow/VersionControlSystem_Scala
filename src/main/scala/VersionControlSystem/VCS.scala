package VersionControlSystem

import java.io.BufferedReader

enum Operation:
  case Insertion
  case Deletion

/*
  Class containing the main functionality of the Version Control System
*/
class VCS:
  /*
    Finds the differences between two versions of the same file.
    The found differences are saved as instance of the 'Diff' class.

    INFO:
    A practical search length before aborting has to be determined.
  */
  def generateDiffForFile(pathA : String, pathB : String) : Diff = {
    val diff : Diff = Diff()
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