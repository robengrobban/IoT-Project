<?php
/**
 * @author Robert Englund
 */

class Route {

	private string $method;
	private string $uri;
	private string $controller;
	private string $function;

	private function __construct() {}

	private static array $routes = [
		'GET' => [],
		'POST' => [],
		'PUT' => [],
		'PATCH' => [],
		'DELETE' => []
	];

	private static function assign($uri, $method, $controller, $function): Route {
		$uri = Http::breakUri($uri);
		if ( mb_substr($uri['path'], "-1") !== "/" ) {
			$uri['path'] .= "/";
		}
		$uri = Http::buildUri($uri);

		$route = new Route();
		$route->uri = $uri;
		$route->method = $method;
		$route->controller = $controller;
		$route->function = $function;

		self::$routes[$method][] = $route;

		return $route;
	}

	public static function get($path, $controller, $function) : Route {
		return self::assign($path, "GET", $controller, $function);
	}
	public static function post($path, $controller, $function) : Route {
		return self::assign($path, "POST", $controller, $function);
	}
	public static function put($path, $controller, $function) : Route {
		return self::assign($path, "PUT", $controller, $function);
	}
	public static function patch($path, $controller, $function) : Route {
		return self::assign($path, "PATCH", $controller, $function);
	}
	public static function delete($path, $controller, $function) : Route {
		return self::assign($path, "DELETE", $controller, $function);
	}

	public static function dispatch($uri, $method) : string {

		$uri = Http::breakUri($uri);
		foreach ($uri as &$part) {
			$part = urldecode($part);
		}
		if ( mb_substr($uri['path'], "-1") !== "/" ) {
			$uri['path'] .= "/";
			$uri = Http::buildUri($uri);
			Response::redirect($uri);
			exit();
		}

		$routes = self::$routes[$method];
		$selectedRoute = null;

		foreach( $routes as $route ) {
			if ( $route->uri !== $uri['path'] ) {
				continue;
			}
			$selectedRoute = $route;
			break;
		}

		if ( is_null($selectedRoute) ) {
			Response::codeNotFound();
			exit();
		}

		return Dispatcher::process($selectedRoute);
	}

	public function getMethod(): string
	{
		return $this->method;
	}

	public function getUri(): string
	{
		return $this->uri;
	}

	public function getController(): string
	{
		return $this->controller;
	}

	public function getFunction(): string
	{
		return $this->function;
	}

}