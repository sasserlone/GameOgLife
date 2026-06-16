/**
 * Client-seitige Logik für Conway's Spiel des Lebens.
 */
class GameOfLife {
    constructor() {
        this.canvas = document.getElementById('gameCanvas');
        this.ctx = this.canvas.getContext('2d');
        this.running = false;
        this.intervalId = null;
        this.cellSize = 15;

        this.initEventListeners();
        this.loadState();
    }

    initEventListeners() {
        document.getElementById('btnStartStop').addEventListener('click', () => this.toggleStartStop());
        document.getElementById('btnStep').addEventListener('click', () => this.step());
        document.getElementById('btnReset').addEventListener('click', () => this.reset());
        document.getElementById('btnRandom').addEventListener('click', () => this.randomize());
        document.getElementById('btnResize').addEventListener('click', () => this.resize());

        document.getElementById('speedSlider').addEventListener('input', (e) => {
            document.getElementById('speedValue').textContent = e.target.value;
            if (this.running) {
                this.stop();
                this.start();
            }
        });

        this.canvas.addEventListener('click', (e) => this.handleCanvasClick(e));
    }

    getSpeedMs() {
        const speed = parseInt(document.getElementById('speedSlider').value);
        // Speed 1 = 500ms, Speed 10 = 50ms
        return 550 - speed * 50;
    }

    async fetchState() {
        const response = await fetch('/api/state');
        return response.json();
    }

    async post(endpoint, body = null) {
        const options = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) {
            options.body = JSON.stringify(body);
        }
        const response = await fetch(endpoint, options);
        return response.json();
    }

    async loadState() {
        try {
            const state = await this.fetchState();
            this.updateCanvas(state);
        } catch (err) {
            console.error('Fehler beim Laden des Zustands:', err);
        }
    }

    async toggleStartStop() {
        if (this.running) {
            this.stop();
        } else {
            this.start();
        }
    }

    start() {
        this.running = true;
        const btn = document.getElementById('btnStartStop');
        btn.textContent = 'Stopp';
        btn.classList.add('running');

        const runStep = async () => {
            if (!this.running) return;
            try {
                const state = await this.post('/api/step');
                this.updateCanvas(state);
                this.intervalId = setTimeout(runStep, this.getSpeedMs());
            } catch (err) {
                console.error('Fehler beim Schritt:', err);
                this.stop();
            }
        };

        runStep();
    }

    stop() {
        this.running = false;
        if (this.intervalId) {
            clearTimeout(this.intervalId);
            this.intervalId = null;
        }
        const btn = document.getElementById('btnStartStop');
        btn.textContent = 'Start';
        btn.classList.remove('running');
    }

    async step() {
        try {
            const state = await this.post('/api/step');
            this.updateCanvas(state);
        } catch (err) {
            console.error('Fehler beim Schritt:', err);
        }
    }

    async reset() {
        if (this.running) this.stop();
        try {
            const state = await this.post('/api/reset');
            this.updateCanvas(state);
        } catch (err) {
            console.error('Fehler beim Zurücksetzen:', err);
        }
    }

    async randomize() {
        if (this.running) this.stop();
        try {
            const state = await this.post('/api/randomize');
            this.updateCanvas(state);
        } catch (err) {
            console.error('Fehler beim Randomisieren:', err);
        }
    }

    async resize() {
        if (this.running) this.stop();
        const rows = parseInt(document.getElementById('inputRows').value);
        const cols = parseInt(document.getElementById('inputCols').value);
        try {
            const state = await this.post('/api/resize', { rows, cols });
            this.updateCanvas(state);
        } catch (err) {
            console.error('Fehler beim Größenändern:', err);
        }
    }

    async handleCanvasClick(event) {
        const rect = this.canvas.getBoundingClientRect();
        const scaleX = this.canvas.width / rect.width;
        const scaleY = this.canvas.height / rect.height;
        const x = (event.clientX - rect.left) * scaleX;
        const y = (event.clientY - rect.top) * scaleY;

        if (!this.state) return;
        const col = Math.floor(x / this.cellSize);
        const row = Math.floor(y / this.cellSize);

        if (row >= 0 && row < this.state.rows && col >= 0 && col < this.state.cols) {
            try {
                const state = await this.post('/api/toggle', { row, col });
                this.updateCanvas(state);
            } catch (err) {
                console.error('Fehler beim Umschalten:', err);
            }
        }
    }

    updateCanvas(state) {
        this.state = state;
        const { rows, cols, grid, generation } = state;

        // Canvas-Größe anpassen
        const maxWidth = Math.min(window.innerWidth - 40, 840);
        const canvasWidth = cols * this.cellSize;
        const canvasHeight = rows * this.cellSize;

        this.canvas.width = canvasWidth;
        this.canvas.height = canvasHeight;
        this.canvas.style.maxWidth = maxWidth + 'px';

        document.getElementById('generationDisplay').textContent = generation;

        // Grid zeichnen
        this.ctx.clearRect(0, 0, canvasWidth, canvasHeight);

        // Hintergrund
        this.ctx.fillStyle = '#0a0a1a';
        this.ctx.fillRect(0, 0, canvasWidth, canvasHeight);

        // Lebende Zellen
        this.ctx.fillStyle = '#00d2ff';
        for (let r = 0; r < rows; r++) {
            for (let c = 0; c < cols; c++) {
                if (grid[r][c]) {
                    this.ctx.fillRect(
                        c * this.cellSize + 1,
                        r * this.cellSize + 1,
                        this.cellSize - 2,
                        this.cellSize - 2
                    );
                }
            }
        }

        // Gitterlinien
        this.ctx.strokeStyle = '#1a1a3e';
        this.ctx.lineWidth = 0.5;
        for (let r = 0; r <= rows; r++) {
            this.ctx.beginPath();
            this.ctx.moveTo(0, r * this.cellSize);
            this.ctx.lineTo(canvasWidth, r * this.cellSize);
            this.ctx.stroke();
        }
        for (let c = 0; c <= cols; c++) {
            this.ctx.beginPath();
            this.ctx.moveTo(c * this.cellSize, 0);
            this.ctx.lineTo(c * this.cellSize, canvasHeight);
            this.ctx.stroke();
        }
    }
}

// App starten, sobald das DOM geladen ist
document.addEventListener('DOMContentLoaded', () => {
    new GameOfLife();
});
