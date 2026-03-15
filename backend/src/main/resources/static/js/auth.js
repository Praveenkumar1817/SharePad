const auth = {
    user: null,

    async checkAuth() {
        try {
            const response = await fetch('/api/auth/me', { credentials: 'include' });
            const data = await response.json();

            if (data.loggedIn) {
                this.user = { email: data.email, name: data.name };
                document.getElementById('user-name').textContent = data.name;
                document.getElementById('user-info').classList.remove('hidden');
                document.getElementById('login-btn').classList.add('hidden');

                // If on a note page, enable actions
                if (window.currentNoteKey) {
                    document.getElementById('action-section').classList.remove('hidden');
                }
            } else {
                this.user = null;
                document.getElementById('login-btn').onclick = () => {
                    window.location.href = data.url;
                };
            }
        } catch (error) {
            console.error('Authentication check failed:', error);
        }
    },

    logout() {
        // Form a POST request to Spring Security logout endpoint if CSRF is disabled
        fetch('/logout', { method: 'POST', credentials: 'include' }).then(() => {
            window.location.reload();
        });
    }
};

document.getElementById('logout-btn').addEventListener('click', () => auth.logout());

// Initial check is now handled via explicit call or deferred in main apps
// auth.checkAuth(); 
window.auth = auth;
