package io.github.patrickyin.beteasy.horsesListGenerator.caulfieldrace

import io.github.patrickyin.beteasy.horsesListGenerator.Processor
import io.github.patrickyin.beteasy.horsesListGenerator.exception.NoSuchNodeException
import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.reactivex.Single
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.Node
import org.dom4j.io.SAXReader
import java.io.StringReader

// The naming of this class is not based on file type
// since it might have the same type input file with different content format(e.g. different xml structure).
class CaulfieldRaceDataParser(private val saxReader: SAXReader) : Processor<ProcessingData> {
  override fun process(input: ProcessingData): Single<ProcessingData> =
    Single.create { emitter ->
      val (fileContent, horses) = input

      val document = read(fileContent) ?: emitter.onSuccess(input).run { return@create }

      val raceNode = document.selectSingleNode("meeting/races/race")
      val horseNodes = raceNode.selectNodes("horses/horse")
        .map {
          val number = it.valueOf("number")
          val name = it.valueOf("@name")

          if(name.isEmpty()) throw NoSuchNodeException()

          findPriceByNumber(number, raceNode)?.let { price ->
            Horse(name, price)
          } ?: throw NoSuchNodeException()
        }

      emitter.onSuccess(fileContent to horses + horseNodes)
    }

  private fun findPriceByNumber(number: String, raceNode: Node): String? =
    raceNode.selectSingleNode("prices/price/horses/horse[@number='$number']")
      ?.valueOf("@Price")

  private fun read(content: String): Document? =
    try {
      val document = saxReader.read(StringReader(content))

      // Verify the input format
      document.selectSingleNode("meeting/races/race/horses/horse")?.run { document }
    } catch (exception: DocumentException) {
      null
    }
}
