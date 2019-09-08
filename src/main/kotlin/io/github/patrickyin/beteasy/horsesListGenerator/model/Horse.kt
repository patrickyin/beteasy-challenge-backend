package io.github.patrickyin.beteasy.horsesListGenerator.model

import java.math.BigDecimal

data class Horse(val name: String, val price: BigDecimal) {
  constructor(name: String, price: String): this(name, price.toBigDecimal())
  constructor(name: String, price: Double): this(name, price.toBigDecimal())
}
