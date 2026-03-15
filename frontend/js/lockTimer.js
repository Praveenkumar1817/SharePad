const lockTimer = {
    intervalId: null,
    endTime: null,
    onExpireCallback: null,

    start(lockedUntilTimestamp, onExpire) {
        this.stop();
        // Since we now receive Epoch millis (Long) from the backend, 
        // we can pass it directly to new Date() or just use it as a number.
        this.endTime = new Date(lockedUntilTimestamp).getTime();
        this.onExpireCallback = onExpire || null;
        this.callbackFired = false;
        
        // Show UI immediately
        document.getElementById('lock-timer').classList.remove('hidden');
        this.updateDisplay();

        this.intervalId = setInterval(() => {
            this.updateDisplay();
        }, 1000);
    },

    stop() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
            this.intervalId = null;
        }
        this.onExpireCallback = null;
        this.callbackFired = false;
        document.getElementById('lock-timer').classList.add('hidden');
    },

    updateDisplay() {
        const now = new Date().getTime();
        const distance = this.endTime - now;

        if (distance <= 0) {
            document.getElementById('lock-timer').innerText = "EXPIRED";
            
            // Only fire callback if we aren't too far past the deadline 
            // (e.g., if it's already 5s past, we probably already reloaded or are in a loop)
            // AND ensure it only fires once.
            if (!this.callbackFired && this.onExpireCallback) {
                if (distance > -5000) { // 5 second grace period for drift
                    console.log("Lock expired, triggering reload.");
                    this.callbackFired = true;
                    setTimeout(this.onExpireCallback, 1000);
                } else {
                    console.log("Lock significantly past expiry, skipping reload trigger to avoid loops.");
                    this.callbackFired = true; // Mark as fired so we don't try again
                }
            }
            return;
        }

        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        document.getElementById('lock-timer').innerText =
            `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    }
};

window.lockTimer = lockTimer;
