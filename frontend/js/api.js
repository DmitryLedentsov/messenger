console.log('messenger api loaded!!');
function MessengerApi(options) {
    if (!$) throw new Error("jquery not imported");
    if (!StompJs) throw new Error("stompjs not imported");
    const signInUrl = `/auth/signin`;
    const signUpUrl = `/auth/signup`;

    const topicUrl = '/topic';
    const publishUrl = '/app';

    var ajaxHeaders = [];
    this.isTokenExpired = false; // Add flag to track token expiration

    this.authUser = async function (user) {
        response = await this.query('post', signInUrl, user);
        if (response) this.setAuth(response);
        return response;
    }

    this.registerUser = async function (user) {
        return await this.query('post', signUpUrl, user);
    }

    this.setAuth = (login) => {
        ajaxHeaders = {
            Authorization: `Bearer ${login.token}`
        };
        this.isTokenExpired = false; // Reset flag on successful auth
    }

    this.toUrlParams = (obj) => {
        var str = [];
        for (var p in obj)
            if (obj.hasOwnProperty(p)) {
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
            }
        return str.length > 0 ? '?' + str.join("&") : '';
    }

    this.query = async function (method, path, data, headers = ajaxHeaders) {
        if (this.isTokenExpired) throw this.lastError; // Skip if token is expired
        let response = null;
        console.log(`sending ${method} request on ${options.serverUrl}${path} ${data ? JSON.stringify(data) : ''}`);
        let request = {
            url: `${options.serverUrl}${path}`,
            method: method,
            headers: headers,
            dataType: 'json',
            data: JSON.stringify(data),
            contentType: 'application/json',
        };
        try {
            response = await $.ajax(request);
            return response;
        } catch (e) {
            if (e.status == 200) return;
            let error = e.responseJSON;
            if (error == null) error = { message: "connection error", error: 'ConnectionError' };
            if (error.error === 'TokenExpiredException') {
                this.isTokenExpired = true; // Set flag to block further requests
                this.socketClientDisconnect(); // Disconnect WebSocket
            }
            this.lastError=error;
            options.onError(error);
            throw error;
        }
    }

    this.setOptions = (o) => {
        options = o;
    }

    this.getOptions = () => options;

    this.init = (login) => {
        this.initLogic(login);
        this.initSocketClient(login);
    }

    this.initSocketClient = (login) => {
        this.setAuth(login);
        if (this.client) {
            this.socketClientDisconnect();
            this.client = null;
        }
        this.client = new StompJs.Client({
            brokerURL: options.brokerUrl,
            connectHeaders: ajaxHeaders
        });

        this.client.onConnect = (frame) => {
            this.socketClientSubscribe(`/user/${login.userId}/error`, (m) => {
                let error = JSON.parse(m.body || '{}');
                if (error.error === 'TokenExpiredException' && !this.isTokenExpired) {
                    this.isTokenExpired = true;
                    this.socketClientDisconnect();
                    options.onError(error);
                } else if (!this.isTokenExpired) {
                    alert(error.message || 'Unknown error');
                }
            });

            this.initSocketLogic(login.userId);
            options.onConnect && options.onConnect(frame);
        };

        this.client.onWebSocketError = (error) => {
            if (this.isTokenExpired) return; // Skip if token is expired
            options.onError && options.onError({ message: "connection error", error: 'ConnectionError' });
            console.error('Error with websocket', error);
        };

        this.client.onStompError = (frame) => {
            if (this.isTokenExpired) return; // Skip if token is expired
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame);
            options.onError && options.onError(frame.headers);
        };

        this.subscribtions = {};
        this.socketClientSubscribe = (topic, callback) => {
            topic = topicUrl + topic;
            console.log('subscribe ' + topic);
            this.subscribtions[topic] = this.client.subscribe(topic, (m) => {
                let data = m.body ? JSON.parse(m.body) : {};
                console.log('receive ' + topic);
                data && console.log(data);
                callback(data);
            });
        }

        this.socketClientUnSubscribe = (topic) => {
            topic = topicUrl + topic;
            if (this.subscribtions[topic]) this.subscribtions[topic].unsubscribe();
        }

        this.socketClientConnect = () => {
            this.client.activate();
        }

        this.socketClientDisconnect = () => {
            this.client.deactivate();
        }

        this.publishSocketMessage = (url, msg) => {
            url = publishUrl + url;
            msg && console.log(msg);
            if (!msg) msg = { aboba: 'aboba' };
            this.client.publish({
                destination: url,
                body: JSON.stringify(msg)
            });
        }
    }

    this.initLogic = (userId) => {
        this.getSelf = async () => {
            return this.query('get', `/user`);
        }
        this.deleteSelf = async () => {
            return this.query('delete', `/user`);
        }
        this.createChat = async (chat) => {
            return this.query('post', `/chat`, chat);
        }
        this.createChatWithoutUsers = async (chat) => {
            return this.query('post', `/chat/create/${chat}`);
        }
        this.editChat = async (chatId, chat) => {
            return this.query('put', `/chat/${chatId}`, chat);
        }
        this.transferChatOwnership = async (chatId, userId) => {
            return this.query('post', `/chat/${chatId}/user/${userId}/transfer-ownership`);
        }
        this.renameChat = async (chatId, name) => {
            return this.query('post', `/chat/${chatId}/set-name/${name}`);
        }
        this.setUserRole = async (chatId, userId, role) => {
            return this.query('post', `/chat/${chatId}/user/${userId}/set-role/${role}`);
        }
        this.getRole = async (chatId, role) => {
            return this.query('get', `/chat/${chatId}/role/${role}`);
        }
        this.deleteChat = async (chatId) => {
            return this.query('delete', `/chat/${chatId}`);
        }
        this.deleteMessage = async (chatId, msgId) => {
            return this.query('delete', `/chat/${chatId}/message/${msgId}`);
        }
        this.deleteMessages = async (chatId) => {
            return this.query('delete', `/chat/${chatId}/messages`);
        }
        this.banUserFromChat = async (userId, chatId) => {
            return this.query('delete', `/chat/${chatId}/user/${userId}`);
        }
        this.addUserInChat = async (chatId, user) => {
            return this.query('post', `/chat/${chatId}/user/${user}`);
        }
        this.sendMessageToChat = async (chatId, msg) => {
            msg.senderId = userId;
            return this.query('post', `/chat/${chatId}/send`, { message: msg });
        }
        this.getUserInChat = async (chatId, userId) => {
            return this.query('get', `/chat/${chatId}/user/${userId}`);
        }
        this.getUserInChatByLogin = async (chatId, userLogin) => {
            return this.query('get', `/chat/${chatId}/user-by-login/${userLogin}`);
        }
        this.getUsersInChat = async (chatId, params = null) => {
            return this.query('get', `/chat/${chatId}/users` + this.toUrlParams(params));
        }
        this.findUsersInChat = async (chatId, name) => {
            let params = { page: 0, limit: 1, filter: name };
            return await this.getUsersInChat(chatId, params);
        }
        this.getAllRolesInChat = async (chatId) => {
            return this.query('get', `/chat/${chatId}/roles`);
        }
        this.getChats = async (params = null) => {
            return this.query('get', `/chats` + this.toUrlParams(params));
        }
        this.findChats = async (name) => {
            let params = { page: 0, limit: 1000000, filter: name };
            return this.getChats(params);
        }
        this.getChat = async (chatId) => {
            return this.query('get', `/chat/${chatId}`);
        }
        this.getMessagesFromChat = async (chatId, params = null) => {
            return this.query('get', `/chat/${chatId}/messages` + this.toUrlParams(params));
        }

        this.sendNotification = async (chatId, userId, notification) => {
            return this.query('post', `/chat/${chatId}/user/${userId}/notifications`, notification);
        }
        this.getNotifications = async (params = null) => {
            return this.query('get', `/notifications` + this.toUrlParams(params));
        }
        this.clearNotifications = async () => {
            return this.query('delete', `/notifications`);
        }
        this.deleteNotification = async (id) => {
            return this.query('delete', `/notification/${id}`);
        }
    }

    this.initSocketLogic = (userId) => {
        this.chats = [];
        this.subscribeOnChats = (onReceive) => {
            this.socketClientSubscribe(`/user/${userId}/chats`, (m) => {
                let op = m.operation;
                let data = m.data;
                onReceive && onReceive(m);
                if (op === "DELETE") {
                    this.unsubscribeOnMessagesInChat(data.id);
                }
            });
        }

        this.subscribeOnNotifications = (onReceive) => {
            this.socketClientSubscribe(`/user/${userId}/notifications`, (m) => {
                onReceive && onReceive(m);
            });
        }
        this.unsubscribeOnMessagesInChat = (chatId) => {
            this.socketClientUnSubscribe(`/chat/${chatId}/messages`);
        }
        this.subscribeOnMessagesInChat = (chatId, onReceive) => {
            this.socketClientSubscribe(`/chat/${chatId}/messages`, (m) => {
                onReceive && onReceive(m);
            });
        }
        this.subscribeOnMessagesInChats = (ids, onReceive) => {
            ids.forEach(el => {
                this.subscribeOnMessagesInChat(el, onReceive);
            });
        }
    }
}