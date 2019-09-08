package io.github.patrickyin.beteasy.horsesListGenerator.processor

import io.github.patrickyin.beteasy.horsesListGenerator.Processor
import io.github.patrickyin.beteasy.horsesListGenerator.caulfieldrace.CaulfieldRaceDataParser
import io.github.patrickyin.beteasy.horsesListGenerator.exception.NoSuchNodeException
import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.github.patrickyin.beteasy.testutil.TestUtils
import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.dom4j.io.SAXReader
import org.junit.Before
import org.junit.Test

class CaulfieldRaceDataParserTest {
  companion object {
    const val VALID_INPUT_FILE = "Caulfield_Race1.xml"
    const val VALID_INPUT_FILE_NO_HORSE_NODE = "Caulfield_Race1-no-horse-node.xml"
    const val VALID_INPUT_FILE_NO_PRICE_NODE = "Caulfield_Race1-no-price-node.xml"
    const val VALID_INPUT_FILE_NO_NAME_ATTRIBUTE = "Caulfield_Race1-no-name-attribute.xml"
  }

  private val saxReader: SAXReader = SAXReader()

  private lateinit var testingData: String

  private lateinit var input: ProcessingData

  private lateinit var processor: Processor<ProcessingData>

  private lateinit var testObserver: TestObserver<ProcessingData>

  @Before
  fun setUp() {
    testingData = TestUtils.readTestingFile(VALID_INPUT_FILE)
    input = ProcessingData(testingData, emptyList())

    testObserver = TestObserver()
    processor = CaulfieldRaceDataParser(saxReader)
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
    assertThat(firstHorse.price).isEqualByComparingTo("4.2")
  }

  @Test
  fun `should emit the horses list with horse name`() {
    processor.process(input).subscribe(testObserver)

    val firstHorse = testObserver.values()[0].second[0]

    assertThat(firstHorse.name).isEqualTo("Advancing")
  }

  @Test
  fun `should emit original input when input xml file is not valid`() {
    input = ProcessingData("{}", emptyList())
    processor.process(input).subscribe(testObserver)

    // Kotlin compiler generates equals function for all data class automatically.
    // So it won't compare memory address.
    testObserver.assertValue { it == input }
  }

  @Test
  fun `should emit original horses list when input xml file is valid and the file does not contain the node`() {
    testingData = TestUtils.readTestingFile(VALID_INPUT_FILE_NO_HORSE_NODE)
    input = ProcessingData(testingData, emptyList())

    processor.process(input).subscribe(testObserver)

    testObserver.assertValue { it.second.isEmpty() }
  }

  @Test
  fun `should emit an error when input xml file is valid and the file does not contain price node`() {
    testingData = TestUtils.readTestingFile(VALID_INPUT_FILE_NO_PRICE_NODE)
    input = ProcessingData(testingData, emptyList())
    processor.process(input).subscribe(testObserver)

    testObserver.assertError(NoSuchNodeException::class.java)
  }

  @Test
  fun `should emit an error when input xml file is valid and the horse node does not contain name attribute`() {
    testingData = TestUtils.readTestingFile(VALID_INPUT_FILE_NO_NAME_ATTRIBUTE)
    input = ProcessingData(testingData, emptyList())
    processor.process(input).subscribe(testObserver)

    testObserver.assertError(NoSuchNodeException::class.java)
  }
}
