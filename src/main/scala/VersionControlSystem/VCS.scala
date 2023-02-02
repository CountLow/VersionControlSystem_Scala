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
    println(result)
  }

  def findDifferences(streamA : BufferedReader, streamB : BufferedReader) : List[(Int, Int)]=
  {
    // Finds index pair (ind_A, ind_B) for each line in streamA
    // If (ind_A != ind_B) or (no Line found for A or B) => change

    var indexes : List[(Int, Int)] = List()
    indexes = indexes :+ (0,0)

    var A : String = streamA.readLine()
    var B : String = streamB.readLine()

    while(A != null || B != null) {
      // Increase both indexes by 1
      if (A == B) {
        indexes = indexes :+ (indexes.last._1 + 1, indexes.last._2 + 1)
      }

      // Search for next occurrence
      else
      {
        streamB.mark(10000)
        B = streamB.readLine()
        var indexDifference : Int = 1

        while(A != B && B != null)
          {
            indexDifference += 1
            B = streamB.readLine()
          }

        //Insertion
        if(B != null) {
          indexes = indexes :+ (indexes.last._1, indexes.last._2 + indexDifference)
          println("Insertion")

        }
        //Deletion
        else {
          indexes = indexes :+ (indexes.last._1 + 1, indexes.last._2)
          streamB.reset()
          println("Deletion")
        }
      }

      A = streamA.readLine()
      if (B != null)
        B = streamB.readLine()
    }

    return indexes
  }
