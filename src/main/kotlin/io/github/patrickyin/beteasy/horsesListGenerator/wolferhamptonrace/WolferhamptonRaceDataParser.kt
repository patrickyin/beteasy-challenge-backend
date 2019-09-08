package io.github.patrickyin.beteasy.horsesListGenerator.wolferhamptonrace

import io.github.patrickyin.beteasy.horsesListGenerator.Processor
import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.github.patrickyin.beteasy.horsesListGenerator.wolferhamptonrace.model.WolferhamptonRace
import io.reactivex.Single
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecodingException

// The naming of this class is not based on file type
// since it might have the same type input file with different content format(e.g. different json structure).
class WolferhamptonRaceDataParser(private val json: Json) : Processor<ProcessingData> {
  override fun process(input: ProcessingData): Single<ProcessingData> = Single.create { emitter ->
    val (fileContent, horses) = input

    val wolferhamptonRace = parseJson(WolferhamptonRace.serializer(), fileContent)
      ?: emitter.onSuccess(input).run { return@create }

    val horseNodes = wolferhamptonRace.rawData.markets.flatMap { it.selections }
      .map { Horse(it.tags.name, it.price) }

    emitter.onSuccess(fileContent to horses + horseNodes)
  }

  // We can extract json parser if we have multiple different type json data source
  // The function returns null to let the whole pipeline continue when the file is an invalid json file or can not be deserialized
  private fun <T> parseJson(deserializationStrategy: DeserializationStrategy<T>, input: String): T? = try {
    json.parse(deserializationStrategy, input)
  } catch (e: KotlinReflectionNotSupportedError) {
    null
  } catch (e: JsonDecodingException) {
    null
  }
}
