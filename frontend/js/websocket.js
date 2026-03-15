const ws = {
    stompClient: null,
    noteKey: null,
    onEditReceived: null,

    connect(noteKey, onEditReceivedCallback) {
        this.noteKey = noteKey;
        this.onEditReceived = onEditReceivedCallback;

        const url = `${window.location.protocol}//${window.location.host}/ws`;
        const socket = new SockJS(url);
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = null; // Disable debug logging

        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            this.stompClient.subscribe(`/topic/note/${noteKey}`, (message) => {
                const editMessage = JSON.parse(message.body);
                if (editMessage.senderEmail !== window.auth.user?.email) {
                    this.onEditReceived(editMessage.content);
                }
            });
        });
    },

    disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
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
