package com.gameoflife

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST-Controller für das Spiel des Lebens.
 */
@RestController
@RequestMapping("/api")
class GameController {

    private val engine = GameEngine()
    private var generation = 0

    /**
     * Gibt den aktuellen Spielzustand zurück.
     */
    @GetMapping("/state")
    fun getState(): GameState {
        val grid = engine.getGrid().map { it.toList() }
        return GameState(engine.rows, engine.cols, grid, generation)
    }

    /**
     * Berechnet die nächste Generation.
     */
    @PostMapping("/step")
    fun step(): GameState {
        engine.step()
        generation++
        return getState()
    }

    /**
     * Setzt das Spiel zurück.
     */
    @PostMapping("/reset")
    fun reset(): GameState {
        engine.reset()
        generation = 0
        return getState()
    }

    /**
     * Füllt das Grid mit zufälligen Zellen.
     */
    @PostMapping("/randomize")
    fun randomize(): GameState {
        engine.randomize()
        generation = 0
        return getState()
    }

    /**
     * Schaltet den Zustand einer Zelle um.
     */
    @PostMapping("/toggle")
    fun toggleCell(@RequestBody request: ToggleRequest): GameState {
        engine.toggleCell(request.row, request.col)
        return getState()
    }

    /**
     * Setzt eine bestimmte Zelle.
     */
    @PostMapping("/set")
    fun setCell(@RequestBody request: SetCellRequest): GameState {
        engine.setCell(request.row, request.col, request.alive)
        return getState()
    }

    /**
     * Ändert die Grid-Größe.
     */
    @PostMapping("/resize")
    fun resize(@RequestBody request: ResizeRequest): GameState {
        engine.resize(request.rows, request.cols)
        generation = 0
        return getState()
    }
}
