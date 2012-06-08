// A Node.js server program that performs procedures and queries state predicates through HTTP interface
var nodeUtils = require('./node-util');
var fs = require('fs');
var http = require('http');
var urlParse = require('url').parse;

var DI = new nodeUtils.DI("nodalion");
DI.readJSONStream(fs.createReadStream(__dirname + "/nodalion.json"), "config");

// Create the domain domains collection
DI.on(['config'], function(DI) {
	DI.setValue('domains', require('./domains')(DI.config.couchURL));
});

// HTTP server (frontend):
// The HOST header determines the domain.
// GET -> Fetches static content for the domain
// POST -> Run a service
DI.on(['config', 'domains'], function(DI) {
	var server = http.createServer(function(req, res) {
		if(req.method == "GET") {
			// Proxy request to static content
			var clientOpt = urlParse(DI.config.couchURL + "/static/" + req.headers.host + req.url);
			clientOpt.headers = req.headers;
			http.get(clientOpt, function(clientRes) {
				res.writeHead(clientRes.statusCode, "Something", clientRes.headers);
				clientRes.pipe(res);
			});
		} else if(req.method == "POST") {
			var domName = req.headers.host;
			var reqDI = new nodeUtils.DI("req");
			DI.domains.requestDomain(domName);
			reqDI.copy(DI.domains, domName + "_logic", "logic");
			reqDI.copy(DI.domains, domName + "_db", "db");
			reqDI.readJSONStream(req, "request");
			reqDI.on(["logic", "request", "db"], function(reqDI) {
				try {
					reqDI.logic.runProcedure(["cjs#runService", reqDI.logic.addReferences(reqDI.request, []), req, res]);
				} catch(e) {
					res.writeHead(500, "Unknown Server Error", {"content-type": "text/plain"});
					res.end("Unknown Server Error: " + e);
				}
			});
		} else {
			res.writeHead(405, "Method Not Allowed", {"content-type": "text/plain"});
			res.end("Method Not Allowed");
		}
	});
	server.listen(DI.config.frontend.listen);
	DI.setValue('frontend', server);
});
