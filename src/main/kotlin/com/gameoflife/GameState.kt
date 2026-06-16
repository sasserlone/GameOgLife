package com.gameoflife

/**
 * Data-Class für den aktuellen Spielzustand (für die REST-API).
 */
data class GameState(
    val rows: Int,
    val cols: Int,
    val grid: List<List<Boolean>>,
    val generation: Int
)

/**
 * Anfrage zum Umschalten einer Zelle.
 */
data class ToggleRequest(
    val row: Int,
    val col: Int
)

/**
 * Anfrage zum Ändern der Grid-Größe.
 */
data class ResizeRequest(
    val rows: Int,
    val cols: Int
)

/**
 * Anfrage zum Setzen einer Zelle.
 */
data class SetCellRequest(
    val row: Int,
    val col: Int,
    val alive: Boolean
)
