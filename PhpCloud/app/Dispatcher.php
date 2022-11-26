<?php
/**
 * @author Robert Englund
 */

class Dispatcher {

	private function __construct() {}

	public static function process(Route $route) : mixed
	{

		$class = $route->getController();
		$controller = new $class();
		$function = $route->getFunction();

		return $controller->$function();
	}

}