package io.github.patrickyin.beteasy.horsesListGenerator

import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class HorsesListObserverTest {
  @MockK(relaxed = true)
  private lateinit var exception: Exception

  private lateinit var horsesListObserver: HorsesListObserver

  private val horse1 = Horse("name 1", "20.12")
  private val horse2 = Horse("name 2", "12")
  private val horse3 = Horse("name 3", "25")

  private val horses = listOf(horse1, horse2, horse3)

  init {
    MockKAnnotations.init(this)
  }

  @Before
  fun setUp() {
    horsesListObserver = HorsesListObserver()
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `should print sorted name list`() {
    val outContent = ByteArrayOutputStream()
    val originalOut = System.out
    System.setOut(PrintStream(outContent))

    horsesListObserver.onSuccess(horses)

    System.setOut(originalOut)

    assertThat(outContent.toString()).isEqualTo("""name 2
name 1
name 3
""")
  }

  @Test
  fun `should not throw exception when onSuccess is called`() {
    assertThatCode {
      horsesListObserver.onSuccess(horses)
    }.doesNotThrowAnyException()
  }

  @Test
  fun `should print stack trace to console`() {
    horsesListObserver.onError(exception)

    verify { exception.printStackTrace() }
  }
}
