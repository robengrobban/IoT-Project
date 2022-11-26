<?php
/**
 * @author Robert Englund
 */

require("app/App.php");
require("app/Path.php");
require("app/Route.php");
require("app/Http.php");
require("app/Dispatcher.php");
require("app/Data.php");
require("app/DB.php");
require("app/QueryBuilder.php");
require("app/Request.php");
require("app/Response.php");

const SYSTEM_DIR = __DIR__;

Request::start();

Response::codeOK();
Response::contentTypeJSON();

register_shutdown_function(function () {
	DB::close();
});

require("controllers/TrainController.php");

App::registerRoutes();
