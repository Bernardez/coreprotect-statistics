var express = require('express');
var router = express.Router();
const config = require('../config');
var mysql = require('mysql');
var connectionData = {
    host: "localhost",
    user: config.dbuser,
    password: config.dbpassword,
    database: config.database
};

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/stats', function (req, res) {
    res.setHeader('Content-Type', 'application/json');
    var con = mysql.createConnection(connectionData);
    con.connect(function(err) {
        if (err) throw err;
        con.query("SELECT * FROM `" + config.tableStatsPretty + "` s\n" +
            "INNER JOIN " + config.tableUsers + " u\n" +
            "ON s." + config.tableUsers + " = u.rowid", (err, rows, fields) => {
            if (err) throw err;


            var objs = [];
            for (var i = 0;i < rows.length; i++) {
                if (!rows[i].user.startsWith('#')) {
                    delete rows[i]['uuid'];
                    delete rows[i]['time'];
                    delete rows[i]['rowid'];
                    objs.push(rows[i]);
                }
            }
            con.end();
            res.end(JSON.stringify(objs));
        });
    });
});

router.get('/users', function (req, res) {
    res.setHeader('Content-Type', 'application/json');
    var con = mysql.createConnection(connectionData);
    con.connect(function(err) {
        if (err) throw err;
        con.query("SELECT rowid as `0`, " + config.tableUsers + " as `1` FROM " + config.tableUsers + " WHERE " +
            config.tableUsers + " NOT LIKE '#%'", (err, rows, fields) => {
            if (err) throw err;
            var objs = [];
            for (var i = 0;i < rows.length; i++) {
                objs.push([rows[i][0], rows[i][1]])
            }
            con.end();
            res.end(JSON.stringify(objs));
        });
    });
});

router.get('/timeline', function (req, res) {
    timelineParams(req, res)
});

router.get('/timeline/:id', function (req, res) {
    timelineParams(req, res)
})

function timelineParams(req, res) {
    res.setHeader('Content-Type', 'application/json');
    var con = mysql.createConnection(connectionData);
    var userID, blocks
    try {
        var sql
        if (req.query.block === undefined && req.params.id === undefined) {
            // No user AND no block
            sql = "SELECT UNIX_TIMESTAMP(DATE(FROM_UNIXTIME(time))) * 1000 AS `0`, COUNT(*) AS `1` FROM " +
                config.tableBlock + " GROUP BY `0`"
        } else if (req.params.id === undefined) {
            // No user BUT with block
            blocks = req.query.block.replace( /[^a-z]/g, "")
            if (blocks.length < 2) throw 'block is invalid'
            sql = "SELECT UNIX_TIMESTAMP(DATE(FROM_UNIXTIME(time))) * 1000 AS `0`, COUNT(*) AS `1` FROM " +
                config.tableBlock + " WHERE" +
                " type IN(SELECT id FROM " + config.tableMaterialMap + " WHERE material LIKE '%" + blocks + "%') GROUP BY `0`"
        } else if (req.query.block === undefined) {
            // User BUT no block
            userID = parseInt(req.params.id, 10)
            if (userID < 1 || userID > 20000) throw 'Invalid user id or block'
            sql = "SELECT UNIX_TIMESTAMP(DATE(FROM_UNIXTIME(time))) * 1000 AS `0`, COUNT(*) AS `1` FROM " +
                config.tableBlock + " WHERE" +
                "  user = " + userID + " GROUP BY `0`"
        }  else {
            // User AND block
            userID = parseInt(req.params.id, 10)
            if (isNaN(userID) || userID < 1 || userID > 20000) throw 'Invalid user id or block'
            blocks = req.query.block.replace( /[^a-z]/g, "")
            if (blocks.length < 2) throw 'block is invalid'
            sql = "SELECT UNIX_TIMESTAMP(DATE(FROM_UNIXTIME(time))) * 1000 AS `0`, COUNT(*) AS `1` FROM " +
                config.tableBlock + " WHERE" +
                " type IN(SELECT id FROM " + config.tableMaterialMap + " WHERE material LIKE '%" + blocks + "%') AND user = " + userID +
                " GROUP BY `0`"
        }
        con.connect(function(err) {
            if (err) throw err;
            con.query(sql, (err, rows, fields) => {
                if (err) throw err;
                var objs = [];
                for (var i = 0;i < rows.length; i++) {
                    objs.push([rows[i][0], rows[i][1]])
                }
                con.end();
                res.end(JSON.stringify(objs));
            });
        });
    } catch (e) {
        throw 'Invalid user id or block'
    }
}

module.exports = router;
