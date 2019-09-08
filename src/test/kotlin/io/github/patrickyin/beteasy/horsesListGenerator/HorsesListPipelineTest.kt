package io.github.patrickyin.beteasy.horsesListGenerator

import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verifyOrder
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.Subject
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class HorsesListPipelineTest {
  @MockK
  private lateinit var processor1: Processor<ProcessingData>

  @MockK
  private lateinit var processor2: Processor<ProcessingData>

  @MockK
  private lateinit var horse: Horse

  @MockK
  private lateinit var input: ProcessingData

  @MockK
  private lateinit var processedInput1: ProcessingData

  @MockK
  private lateinit var processedInput2: ProcessingData

  @MockK
  private lateinit var inputFileReader: InputFileReader

  @MockK
  private lateinit var file: File

  private lateinit var horses: List<Horse>

  private lateinit var processors: List<Processor<ProcessingData>>

  private lateinit var horsesListPipeline: HorsesListPipeline

  private lateinit var testObserver: TestObserver<List<Horse>>

  init {
    MockKAnnotations.init(this)
  }

  @Before
  fun setUp() {
    horses = listOf(horse)

    every { inputFileReader.read(file) } returns Single.just(input)
    every { processor1.process(input) } returns Single.just(processedInput1)
    every { processor2.process(processedInput1) } returns Single.just(processedInput2)
    every { processedInput2.second } returns horses

    testObserver = TestObserver()
    processors = listOf(processor1, processor2)
    horsesListPipeline = HorsesListPipeline(inputFileReader, processors)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `should provide a producer subject`() {
    assertThat(horsesListPipeline.producer).isInstanceOf(Subject::class.java)
  }

  @Test
  fun `should provide a rx pipeline`() {
    assertThat(horsesListPipeline.pipeline).isInstanceOf(Single::class.java)
  }

  @Test
  fun `should execute processors to the pipeline`() {
    horsesListPipeline.pipeline.subscribe()

    horsesListPipeline.producer.onNext(file)
    horsesListPipeline.producer.onComplete()

    verifyOrder {
      processor1.process(input)
      processor2.process(processedInput1)
    }
  }

  @Test
  fun `should emit result horses list`() {
    horsesListPipeline.pipeline.subscribe(testObserver)

    horsesListPipeline.producer.onNext(file)
    horsesListPipeline.producer.onComplete()

    testObserver.assertValueCount(1)
    testObserver.assertValue { it[0] == horse }
  }

  @Test
  fun `should emit complete event`() {
    horsesListPipeline.pipeline.subscribe(testObserver)

    horsesListPipeline.producer.onNext(file)
    horsesListPipeline.producer.onComplete()

    testObserver.assertComplete()
  }
}
