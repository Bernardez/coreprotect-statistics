var app = require('../app');
var http = require('http')
const config = require('../config');
var port = process.env.PORT || config.port

app.set('port', port);

var httpsServer = http.createServer(app)

httpsServer.listen(port)
console.log('The magic happens on port ' + port)
