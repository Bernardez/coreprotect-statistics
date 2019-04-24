var app = require('../app');
var https = require('https')
var fs = require('fs')
var privateKey  = fs.readFileSync('/etc/letsencrypt/live/ylyl.eu/privkey.pem', 'utf8')
var certificate = fs.readFileSync('/etc/letsencrypt/live/ylyl.eu/fullchain.pem', 'utf8')
var credentials = {key: privateKey, cert: certificate}
const config = require('../config');
var port = process.env.PORT || config.port

app.set('port', port);

var httpsServer = https.createServer(credentials, app)

httpsServer.listen(port)
console.log('The magic happens on port ' + port)
