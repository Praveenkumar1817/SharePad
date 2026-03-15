const API_BASE_URL = 'http://localhost:8080/api';

const api = {
    async fetchNote(noteKey) {
        const response = await fetch(`${API_BASE_URL}/notes/${noteKey}`, { credentials: 'include' });
        if (!response.ok) throw new Error('Failed to fetch note');
        return response.json();
    },

    async toggleReadOnly(noteKey) {
        const response = await fetch(`${API_BASE_URL}/notes/${noteKey}/readonly`, { method: 'POST', credentials: 'include' });
        if (!response.ok) throw new Error('Failed to toggle readonly');
        return response.ok;
    },

    async lockNote(noteKey) {
        const response = await fetch(`${API_BASE_URL}/lock/${noteKey}`, { method: 'POST', credentials: 'include' });
        if (!response.ok) throw new Error('Failed to lock note');
        return response.ok;
    },

    async extendLock(noteKey) {
        const response = await fetch(`${API_BASE_URL}/lock/${noteKey}/extend`, { method: 'POST', credentials: 'include' });
        if (!response.ok) throw new Error('Failed to extend lock');
        return response.ok;
    },

    async unlockNote(noteKey) {
        const response = await fetch(`${API_BASE_URL}/lock/${noteKey}/unlock`, { method: 'POST', credentials: 'include' });
        return response.ok;
    },

    downloadExport(noteKey, format) {
        window.location.href = `${API_BASE_URL}/export/${noteKey}?format=${format}`;
    }
};

window.api = api;
