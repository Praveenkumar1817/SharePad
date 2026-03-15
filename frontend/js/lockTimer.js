const lockTimer = {
    intervalId: null,
    endTime: null,

    start(lockedUntilStr) {
        this.stop();
        this.endTime = new Date(lockedUntilStr).getTime();
        this.updateDisplay();

        document.getElementById('lock-timer').classList.remove('hidden');

        this.intervalId = setInterval(() => {
            this.updateDisplay();
        }, 1000);
    },

    stop() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
            this.intervalId = null;
        }
        document.getElementById('lock-timer').classList.add('hidden');
    },

    updateDisplay() {
        const now = new Date().getTime();
        const distance = this.endTime - now;

        if (distance <= 0) {
            this.stop();
            document.getElementById('lock-timer').innerText = "EXPIRED";
            // In a real app, trigger a refresh to show unlocked state
            setTimeout(() => window.location.reload(), 2000);
            return;
        }

        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        document.getElementById('lock-timer').innerText =
            `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    }
};

window.lockTimer = lockTimer;
