package io.github.patrickyin.beteasy.horsesListGenerator

import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.github.patrickyin.beteasy.testutil.TestUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException

class InputFileReaderTest {
  companion object {
    const val READER_TEST_FILE = "reader-test.json"
  }

  @MockK(relaxed = true)
  private lateinit var file: File

  private lateinit var testObserver: TestObserver<ProcessingData>

  private lateinit var inputFileReader: InputFileReader

  init {
    MockKAnnotations.init(this)
  }

  @Before
  fun setUp() {
    testObserver = TestObserver()
    inputFileReader = InputFileReader()
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `should emit onError when input file is not exist`() {
    every { file.exists() } returns false

    inputFileReader.read(file).subscribe(testObserver)

    testObserver.assertError(FileNotFoundException::class.java)
  }

  @Test
  fun `should not complete when input file is not exist`() {
    every { file.exists() } returns false

    inputFileReader.read(file).subscribe(testObserver)

    testObserver.assertNotComplete()
  }

  @Test
  fun `should emit input string with empty list`() {
    file = TestUtils.getResourceFile(READER_TEST_FILE)
    inputFileReader.read(file).subscribe(testObserver)

    // Kotlin compare object's value using generated equals rather than comparing memory address
    testObserver.assertValue(ProcessingData("""{
  "name": "reader-test"
}""", emptyList()))
  }
}
