const { connected } = require('process');
const app = require('express')();
const http = require('http').createServer(app);
const io = require('socket.io')(http);

var serverIndexURL = '/home/admin/web/iooojik.ru/public_html/sockets/index.html';
var localIndexURL = 'C:/Users/user/Desktop/сокеты/js/index.html';

app.get('/', (req, res) => {
    res.sendFile('/home/admin/web/iooojik.ru/public_html/sockets/index.html');
});

io.on('connection', (socket) => {

    //когда приходит сообщение
    socket.on('chat message', msg => {
    
        var obj = JSON.parse(msg);
        //отправляем в комнату с идентификатором
        sendMessage(obj.unique_room_id, msg);

    });
});

io.on('connection', (socket) => {
    console.log('a user connected');

    socket.on('join', function (data) {
        if(data != null){
            socket.join(data); // Используем комнату socket io
        }
    });

    socket.on('disconnect', () => {
        console.log('user disconnected');
      });
  });


function sendMessage(roomName, message){
    console.log(message)
    io.sockets.in(roomName).emit('chat message', message);
    io.sockets.in(roomName).emit('notification', message);
}

http.listen(3000, ()=>{
    console.log('SERVER IS RUNNING');
});