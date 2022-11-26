<?php
/**
 * @author Robert Englund
 */

class Request
{

	private static Data $data;
	private static string $method;

	private const GET = "GET";
	private const POST = "POST";
	private const PUT = "PUT";
	private const PATCH = "PATCH";
	private const DELETE = "DELETE";

	private function __construct()
	{}

	public static function start() : void
	{
		self::$method = $_SERVER['REQUEST_METHOD'];
		if ( self::$method === self::GET ) {
			self::$data = new Data($_GET);
		}
		else if ( self::$method === self::POST ) {
			self::$data = new Data($_POST);
		}
		else {
			self::$data = new Data(self::readRequest());
		}
	}

	private static function readRequest() : array
	{
		$params = array();
		parse_str(file_get_contents('php://input'), $params);
		return $params;
	}

	public static function getMethod() : string
	{
		return self::$method;
	}

	public static function data() : object
	{
		return self::$data;
	}

	public static function isMethod(string $method) : bool
	{
		return self::$method === $method;
	}

	public static function exists() : bool
	{
		return count(self::data()->all()) !== 0;
	}

	public static function isMobile() : bool
	{
		$ua = strtolower($_SERVER["HTTP_USER_AGENT"]);
		return is_numeric(strpos($ua, "mobile"));
	}

}