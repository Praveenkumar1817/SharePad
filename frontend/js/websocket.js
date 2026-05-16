const ws = {
    stompClient: null,
    noteKey: null,
    onEditReceived: null,

    connect(noteKey, onEditReceivedCallback) {
        // Disconnect existing if any
        this.disconnect();
        
        this.noteKey = noteKey;
        this.onEditReceived = onEditReceivedCallback;

        const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
        const wsBackend = isLocal ? `${window.location.protocol}//${window.location.host}` : 'https://sharepad-87ll.onrender.com';
        const url = `${wsBackend}/ws`;
        const socket = new SockJS(url);
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = null; // Disable debug logging

        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            if (this.stompClient && this.stompClient.connected) {
                this.stompClient.subscribe(`/topic/note/${noteKey}`, (message) => {
                    const editMessage = JSON.parse(message.body);
                    if (editMessage.senderEmail !== window.auth.user?.email) {
                        this.onEditReceived(editMessage.content);
                    }
                });
            }
        });
    },

    disconnect() {
        if (this.stompClient !== null) {
            try {
                if (this.stompClient.connected) {
                    this.stompClient.disconnect();
                }
            } catch (e) {
                console.warn("Error during stomp disconnect", e);
            }
            this.stompClient = null;
        }
        console.log("Disconnected");
    },

    sendEdit(content) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send(`/app/note/${this.noteKey}/edit`, {}, JSON.stringify({
                content: content
            }));
        }
    }
};

window.ws = ws;
