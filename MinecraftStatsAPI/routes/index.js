var express = require('express');
var router = express.Router();


/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/stats', function (req, res) {
    res.setHeader('Content-Type', 'application/json');
    var mysql = require('mysql');

    var con = mysql.createConnection({
        host: "localhost",
        user: "MC_CoreProtect",
        password: "<<PASSWORD>>",
        database: "mc_coreprotect"
    });

    con.connect(function(err) {
        if (err) throw err;
        con.query("SELECT * FROM `mc_stats_pretty` s\n" +
            "INNER JOIN mc_user u\n" +
            "ON s.user = u.rowid", (err, rows, fields) => {
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
})

module.exports = router;
