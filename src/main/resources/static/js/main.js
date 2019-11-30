'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    stompClient.subscribe('/user/queue/reply', onPersonalMessageReceived)
    stompClient.send("/app/chat.enter", {},
        JSON.stringify({
            sender: 'anonymous',
            type: 'JOIN',
            content: username
        })
    );
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function onPersonalMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    if (message.type === 'LOGIN_ACCEPTED') {
        showChat();
        showServerMessageInChat(message.content, 'event-message');
        stompClient.subscribe('/topic/public', onMessageReceived);
    } else if (message.type === 'LOGIN_DECLINED') {
        stompClient.disconnect();
    } else if (message.type === 'LOGIN_REQUIRED') {
        showServerMessageInChat(message.content, 'error-message');
    } else if (message.type === 'COMMAND_ERROR') {
        showServerMessageInChat(message.content, 'error-message');
    } else if (message.type === 'COMMAND_RESULT') {
        showServerMessageInChat(message.content, 'command-result');
    }
}

function showChat() {
    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');
    connectingElement.classList.add('hidden');
}

function showServerMessageInChat(content, style) {
    var messageElement = document.createElement('li');
    messageElement.classList.add(style);

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(content);

    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);

    messageArea.scrollTop = messageArea.scrollHeight;
}

function sendMessage(event) {
    var content = messageInput.value.trim();
    if (content && stompClient) {
        var message = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        if (content.startsWith("/")) {
            stompClient.send("/app/chat.command", {}, JSON.stringify(message));
            messageInput.value = '';
        } else {
            stompClient.send("/app/chat.send", {}, JSON.stringify(message));
            messageInput.value = '';
        }
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);

        var timeElement = document.createElement('span');
        var timeText = document.createTextNode(getCurrentTimeString());
        timeElement.appendChild(timeText);
        timeElement.classList.add('time-element');

        messageElement.appendChild(usernameElement);
        messageElement.appendChild(timeElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);

    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getCurrentTimeString() {
    var today = new Date();
    return withLeadingZero(today.getHours()) + ":" +
        withLeadingZero(today.getMinutes()) + ":" +
        withLeadingZero(today.getSeconds());
}

function withLeadingZero(number) {
    if (number < 10) {
        return "0" + number;
    } else {
        return number;
    }
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
