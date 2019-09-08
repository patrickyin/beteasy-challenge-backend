package io.github.patrickyin.beteasy.horsesListGenerator

import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class HorsesListGeneratorTest {
  @MockK
  private lateinit var horsesListPipeline: HorsesListPipeline

  @MockK
  private lateinit var file1: File

  @MockK
  private lateinit var file2: File

  private lateinit var horsesListObserver: TestObserver<List<Horse>>

  private lateinit var horsesListGenerator: HorsesListGenerator

  init {
    MockKAnnotations.init(this)
  }

  @Before
  fun setUp() {
    val subject = PublishSubject.create<File>()
    // Mock simple data to verify if the data comes up correctly.
    every { file1.name } returns "name1"
    every { file2.name } returns "name2"
    every { horsesListPipeline.producer } returns subject
    every { horsesListPipeline.pipeline } returns subject.map { Horse(it.name, "10") }.toList()

    horsesListObserver = TestObserver()
    horsesListGenerator = HorsesListGenerator(horsesListPipeline, horsesListObserver)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `should emit 2 items to observer through pipeline`() {
    horsesListGenerator.generateFrom(listOf(file1, file2))

    val output = horsesListObserver.values()[0]

    assertThat(output).hasSize(2)
  }

  @Test
  fun `should emit horses to observer through pipeline`() {
    horsesListGenerator.generateFrom(listOf(file1, file2))

    val output = horsesListObserver.values()[0]

    assertThat(output[0]).isEqualTo(Horse("name1", "10"))
    assertThat(output[1]).isEqualTo(Horse("name2", "10"))
  }

  @Test
  fun `should only emit onComplete event to observer through pipeline when input is empty`() {
    horsesListGenerator.generateFrom(listOf())

    horsesListObserver.assertComplete()
  }
}
