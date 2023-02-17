package VersionControlSystem

import java.io.*
import java.nio.*
import scala.jdk.CollectionConverters.*

class VCS:
  def generateDiffForFile(pathA : String, pathB : String) =
  {
    val fileInputA: FileInputStream = FileInputStream(getClass.getResource(pathA).getFile)
    val fileInputB: FileInputStream = FileInputStream(getClass.getResource(pathB).getFile)

    val bufferedReaderA : BufferedReader = BufferedReader(InputStreamReader(fileInputA))
    val bufferedReaderB : BufferedReader = BufferedReader(InputStreamReader(fileInputB))

    val result : List[(Int, Int)] = findDifferences(bufferedReaderA, bufferedReaderB)
    print(result)

    print("Current reader: " + fileInputA.read())
  }

  /*
  Generates instance of Diff from list of indexes.
  Insertion: Save row index of base file and data to be inserted.
  Deletion:  Save row index of base file.

  Ignore indexes indicating no change.
  */
//  def generateDiff(indexDifferences : List[(Int, Int)]) : Diff =
//    {
//      var diff : Diff = Diff()
//
//      var prevIndexPair : (Int, Int) = indexDifferences.head
//
//      for
//        currentIndexPair <- indexDifferences
//      do
//        val differences : (Int, Int) = (currentIndexPair._1 - prevIndexPair._1, currentIndexPair._2 - prevIndexPair._2)
//        prevIndexPair = currentIndexPair
//
//        if(differences._1 > differences._2) // Insertion
//        {
//
//        }
//        else if(differences._1 < differences._2) // Deletion
//        {
//
//        }
//    }

  def findDifferences(streamA : BufferedReader, streamB : BufferedReader) : List[(Int, Int)]=
  {
    // Finds index pair (ind_A, ind_B) for each line in streamA
    // If (ind_A != ind_B) or (no Line found for A or B) => change

    var indexes : List[(Int, Int)] = List()
    indexes = indexes :+ (0,0)

    streamB.mark(1000000)
    var A : String = streamA.readLine()
    var B : String = streamB.readLine()

    while(A != null || B != null) {
      // Increase both indexes by 1
      if (A == B) {
        indexes = indexes :+ (indexes.last._1 + 1, indexes.last._2 + 1)
        streamB.mark(1000000)
      }

      // Search for next occurrence
      else
      {
        B = streamB.readLine()
        var indexDifference : Int = 1

        while(A != B && B != null)
          {
            indexDifference += 1
            B = streamB.readLine()
          }

        //Insertion
        if(B != null || (A == null && B == null)) {
          indexes = indexes :+ (indexes.last._1, indexes.last._2 + indexDifference)
          println("Insertion")
          streamB.mark(1000000)

        }
        //Deletion
        else {
          indexes = indexes :+ (indexes.last._1 + 1, indexes.last._2)
          streamB.reset()
          println("Deletion")
        }
      }

      A = streamA.readLine()
      //if (B != null)
      B = streamB.readLine()
    }

    return indexes
  }
