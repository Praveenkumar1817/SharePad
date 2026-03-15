let currentNoteKey = null;
let noteData = null;
let isEditorLockedByMe = false;

// DOM Elements
const welcomeScreen = document.getElementById('welcome-screen');
const editorScreen = document.getElementById('editor-screen');
const noteNameInput = document.getElementById('note-name-input');
const goToNoteBtn = document.getElementById('go-to-note-btn');
const editor = document.getElementById('note-editor');
const noteNameDisplay = document.getElementById('note-name-display');

const lockedBadge = document.getElementById('locked-badge');

const authSection = document.getElementById('auth-section');
const actionSection = document.getElementById('action-section');

const lockBtn = document.getElementById('lock-btn');
const extendLockBtn = document.getElementById('extend-lock-btn');
const unlockBtn = document.getElementById('unlock-btn');

const exportTxtBtn = document.getElementById('export-txt-btn');
const exportMdBtn = document.getElementById('export-md-btn');
const exportPdfBtn = document.getElementById('export-pdf-btn');

// Initialization
async function initialize() {
    // Wait for auth to be determined before anything else
    await window.auth.checkAuth();

    const path = window.location.pathname;
    const pathParts = path.split('/');

    // Check if URL has note key, e.g., /note/my-note
    // For local static hosting without fancy routing, we use hash or just prompt in UI
    const hash = window.location.hash.slice(1);

    if (hash) {
        await loadNote(hash);
    } else {
        showWelcome();
    }
}

// UI State
function showWelcome() {
    welcomeScreen.classList.remove('hidden');
    editorScreen.classList.add('hidden');
    actionSection.classList.add('hidden');
    noteNameDisplay.textContent = '';
    currentNoteKey = null;
    window.currentNoteKey = null;
}

async function loadNote(noteKey) {
    try {
        currentNoteKey = noteKey;
        window.currentNoteKey = noteKey;
        noteNameDisplay.textContent = `/${noteKey}`;

        noteData = await window.api.fetchNote(noteKey);

        editor.value = noteData.content || '';

        welcomeScreen.classList.add('hidden');
        editorScreen.classList.remove('hidden');
        window.location.hash = noteKey;

        updateUIBasedOnPermissions();

        // Connect WebSocket
        window.ws.connect(noteKey, (newContent) => {
            const cursorStart = editor.selectionStart;
            const cursorEnd = editor.selectionEnd;
            editor.value = newContent;
            // Best-effort cursor preservation, naive
            editor.setSelectionRange(cursorStart, cursorEnd);
        });

    } catch (error) {
        console.error("Failed to load note", error);
        alert("Could not load the note. Please try again.");
        showWelcome();
    }
}

function updateUIBasedOnPermissions() {
    const isLogged = window.auth.user != null;

    // Visibility of actions
    if (isLogged) {
        actionSection.classList.remove('hidden');
    } else {
        actionSection.classList.add('hidden');
    }

    // Badges

    if (noteData.locked) {
        lockedBadge.classList.remove('hidden');
        document.getElementById('locker-email').textContent = noteData.lockedByName;
    } else {
        lockedBadge.classList.add('hidden');
    }

    // Determine editability
    let canEdit = true;

    if (!isLogged) canEdit = false;

    isEditorLockedByMe = false;
    if (noteData.locked) {
        if (isLogged && noteData.lockedByEmail?.toLowerCase() === window.auth.user?.email?.toLowerCase()) {
            isEditorLockedByMe = true;
            canEdit = true;
            // Setup lock UI with auto-reload on expiry
            window.lockTimer.start(noteData.lockedUntil, () => {
                loadNote(currentNoteKey);
            });
            lockBtn.classList.add('hidden');
            extendLockBtn.classList.remove('hidden');
            unlockBtn.classList.remove('hidden');
        } else {
            canEdit = false;
            window.lockTimer.stop();
            lockBtn.classList.add('hidden');
            extendLockBtn.classList.add('hidden');
            unlockBtn.classList.add('hidden');
        }
    } else {
        window.lockTimer.stop();
        if (isLogged) {
            lockBtn.classList.remove('hidden');
            extendLockBtn.classList.add('hidden');
            unlockBtn.classList.add('hidden');
        }
    }

    editor.disabled = !canEdit;
}

// Event Listeners
goToNoteBtn.addEventListener('click', () => {
    const key = noteNameInput.value.trim();
    if (key) loadNote(key);
});
noteNameInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') goToNoteBtn.click();
});

editor.addEventListener('input', () => {
    window.ws.sendEdit(editor.value);
});

// Actions
exportTxtBtn.addEventListener('click', () => window.api.downloadExport(currentNoteKey, 'txt'));
exportMdBtn.addEventListener('click', () => window.api.downloadExport(currentNoteKey, 'md'));
exportPdfBtn.addEventListener('click', () => window.api.downloadExport(currentNoteKey, 'pdf'));


lockBtn.addEventListener('click', async () => {
    try {
        await window.api.lockNote(currentNoteKey);
        await loadNote(currentNoteKey);
    } catch (e) { alert(e.message); }
});

extendLockBtn.addEventListener('click', async () => {
    try {
        await window.api.extendLock(currentNoteKey);
        await loadNote(currentNoteKey);
    } catch (e) { alert(e.message); }
});

unlockBtn.addEventListener('click', async () => {
    try {
        await window.api.unlockNote(currentNoteKey);
        await loadNote(currentNoteKey);
    } catch (e) { alert(e.message); }
});

// Start immediately
initialize();
