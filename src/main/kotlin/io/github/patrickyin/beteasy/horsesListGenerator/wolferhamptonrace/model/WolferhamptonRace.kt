package io.github.patrickyin.beteasy.horsesListGenerator.wolferhamptonrace.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WolferhamptonRace(@SerialName("RawData") val rawData: RawData)

@Serializable
data class RawData(@SerialName("Markets") val markets: List<Market>)

@Serializable
data class Market(@Required @SerialName("Selections") val selections: List<Selection>)

@Serializable
data class Selection(@Required @SerialName("Price") val price: Double, @SerialName("Tags") val tags: Tags)

@Serializable
data class Tags(@Required @SerialName("name") val name: String)
