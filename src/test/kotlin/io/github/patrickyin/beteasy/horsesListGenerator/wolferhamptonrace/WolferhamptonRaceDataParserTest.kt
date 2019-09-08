package io.github.patrickyin.beteasy.horsesListGenerator.wolferhamptonrace

import io.github.patrickyin.beteasy.horsesListGenerator.Processor
import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.github.patrickyin.beteasy.testutil.TestUtils
import io.reactivex.observers.TestObserver
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class WolferhamptonRaceDataParserTest {
  companion object {
    const val VALID_INPUT_FILE = "Wolferhampton_Race1.json"
    const val VALID_INPUT_FILE_NO_SELECTION = "Wolferhampton_Race1-no-selection.json"
    const val VALID_INPUT_FILE_NO_PRICE = "Wolferhampton_Race1-no-price.json"
    const val VALID_INPUT_FILE_NO_NAME = "Wolferhampton_Race1-no-name.json"
  }

  private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

  private lateinit var testingData: String

  private lateinit var input: ProcessingData

  private lateinit var processor: Processor<ProcessingData>

  private lateinit var testObserver: TestObserver<ProcessingData>

  @Before
  fun setUp() {
    testingData = TestUtils.readTestingFile(VALID_INPUT_FILE)
    input = ProcessingData(testingData, emptyList())

    testObserver = TestObserver()
    processor = WolferhamptonRaceDataParser(json)
  }

  @Test
  fun `should emit the horses list`() {
    processor.process(input).subscribe(testObserver)

    testObserver.assertValue { it.second.size == 2 }
  }

  @Test
  fun `should emit the horses list contains input list and new list`() {
    input = ProcessingData(testingData, listOf(Horse("name", "10.0")))
    processor.process(input).subscribe(testObserver)

    testObserver.assertValue { it.second.size == 3 }
  }

  @Test
  fun `should emit 1 result`() {
    processor.process(input).subscribe(testObserver)

    testObserver.assertValueCount(1)
  }

  @Test
  fun `should emit onComplete event`() {
    processor.process(input).subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  fun `should emit the horses list with horse price`() {
    processor.process(input).subscribe(testObserver)

    val firstHorse = testObserver.values()[0].second[0]
    // Using isEqualByComparingTo instead of isEqualTo for BigDecimal
    assertThat(firstHorse.price).isEqualByComparingTo("10.0")
  }

  @Test
  fun `should emit the horses list with horse name`() {
    processor.process(input).subscribe(testObserver)

    val firstHorse = testObserver.values()[0].second[0]

    assertThat(firstHorse.name).isEqualTo("Toolatetodelegate")
  }

  @Test
  fun `should emit original input when input json file is not valid`() {
    input = ProcessingData("<html />", emptyList())
    processor.process(input).subscribe(testObserver)

    // Kotlin compiler generates equals function for all data class automatically.
    // So it won't compare memory address.
    testObserver.assertValue { it == input }
  }

  @Test
  fun `should emit original horses list when input json file is valid and the file does not contain the node`() {
    testingData = TestUtils.readTestingFile(VALID_INPUT_FILE_NO_SELECTION)
    input = ProcessingData(testingData, emptyList())

    processor.process(input).subscribe(testObserver)

    testObserver.assertValue { it.second.isEmpty() }
  }

  @Test
  fun `should emit an error when input json file is valid and the selection does not contain name property`() {
    testingData = TestUtils.readTestingFile(VALID_INPUT_FILE_NO_NAME)
    input = ProcessingData(testingData, emptyList())
    processor.process(input).subscribe(testObserver)

    testObserver.assertError(MissingFieldException::class.java)
  }

  @Test
  fun `should emit an error when input json file is valid and the selection does not contain price property`() {
    testingData = TestUtils.readTestingFile(VALID_INPUT_FILE_NO_PRICE)
    input = ProcessingData(testingData, emptyList())
    processor.process(input).subscribe(testObserver)

    testObserver.assertError(MissingFieldException::class.java)
  }
}
