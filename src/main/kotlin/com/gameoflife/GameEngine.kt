package com.gameoflife

/**
 * Implementiert die Spielregeln von Conway's Game of Life.
 */
class GameEngine(
    var rows: Int = 30,
    var cols: Int = 40
) {
    private var grid: Array<BooleanArray> = Array(rows) { BooleanArray(cols) }

    /**
     * Gibt eine Kopie des aktuellen Grids zurück.
     */
    fun getGrid(): Array<BooleanArray> {
        return grid.map { it.clone() }.toTypedArray()
    }

    /**
     * Gibt den Zustand einer Zelle zurück.
     */
    fun getCell(row: Int, col: Int): Boolean {
        return if (row in 0 until rows && col in 0 until cols) grid[row][col] else false
    }

    /**
     * Setzt den Zustand einer Zelle.
     */
    fun setCell(row: Int, col: Int, alive: Boolean) {
        if (row in 0 until rows && col in 0 until cols) {
            grid[row][col] = alive
        }
    }

    /**
     * Schaltet den Zustand einer Zelle um (tot ↔ lebendig).
     */
    fun toggleCell(row: Int, col: Int): Boolean {
        return if (row in 0 until rows && col in 0 until cols) {
            grid[row][col] = !grid[row][col]
            grid[row][col]
        } else false
    }

    /**
     * Berechnet die nächste Generation basierend auf den Conway-Regeln:
     * 1. Eine lebende Zelle mit weniger als 2 lebenden Nachbarn stirbt (Unterbevölkerung).
     * 2. Eine lebende Zelle mit 2 oder 3 lebenden Nachbarn überlebt.
     * 3. Eine lebende Zelle mit mehr als 3 lebenden Nachbarn stirbt (Überbevölkerung).
     * 4. Eine tote Zelle mit genau 3 lebenden Nachbarn wird lebendig (Fortpflanzung).
     */
    fun step() {
        val newGrid = Array(rows) { BooleanArray(cols) }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val liveNeighbors = countLiveNeighbors(r, c)
                newGrid[r][c] = when {
                    grid[r][c] && liveNeighbors < 2 -> false
                    grid[r][c] && (liveNeighbors == 2 || liveNeighbors == 3) -> true
                    grid[r][c] && liveNeighbors > 3 -> false
                    !grid[r][c] && liveNeighbors == 3 -> true
                    else -> grid[r][c]
                }
            }
        }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                grid[r][c] = newGrid[r][c]
            }
        }
    }

    /**
     * Zählt die lebenden Nachbarn einer Zelle (Moore-Nachbarschaft).
     */
    private fun countLiveNeighbors(row: Int, col: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = row + dr
                val nc = col + dc
                if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc]) {
                    count++
                }
            }
        }
        return count
    }

    /**
     * Setzt das gesamte Grid zurück (alle Zellen tot).
     */
    fun reset() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                grid[r][c] = false
            }
        }
    }

    /**
     * Füllt das Grid mit zufälligen lebenden/toten Zellen.
     */
    fun randomize(liveProbability: Double = 0.3) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                grid[r][c] = Math.random() < liveProbability
            }
        }
    }

    /**
     * Ändert die Grid-Größe und überträgt existierende Zellen.
     */
    fun resize(newRows: Int, newCols: Int) {
        if (newRows <= 0 || newCols <= 0) return
        val oldGrid = grid
        val oldRows = rows
        val oldCols = cols

        rows = newRows
        cols = newCols
        grid = Array(rows) { r ->
            BooleanArray(cols) { c ->
                if (r < oldRows && c < oldCols) oldGrid[r][c] else false
            }
        }
    }
}
