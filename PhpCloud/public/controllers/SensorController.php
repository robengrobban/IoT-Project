<?php

class SensorController
{

	public static function index() : string {
		$data = array('carriage' => [], 'platform' => []);

		$carriageSensors = DB::get("*", "carriage_sensors", "", []);
		if ( !is_null($carriageSensors) ) {
			$data['carriage'] = $carriageSensors;
		}

		$platformSensors = DB::get("*", "platform_sensors", "", []);
		if ( !is_null($platformSensors) ) {
			$data['platform'] = $platformSensors;
		}

		return Response::JSON($data);
	}

	public static function get() : string {
		if ( !Request::params()->nonempty('address') ) {
			Response::codeBadRequest();
			Response::abort();
		}

		// Investigate for carriage
		$carriageSensor = DB::get("*", "carriage_sensors", "address = ?", [Request::params()->get('address')]);
		if ( !empty($carriageSensor) ) {
			return Response::JSON($carriageSensor[0]);
		}

		// Investigate for platform
		$platformSensor = DB::get("*", "platform_sensors", "address = ?", [Request::params()->get('address')]);
		if ( !empty($platformSensor) ) {
			return Response::JSON($platformSensor[0]);
		}

		Response::codeNotFound();
		Response::abort();
	}

	public static function indexCarriage() : string {
		$carriageSensors = DB::get("*", "carriage_sensors", "", []);
		if ( is_null($carriageSensors) ) {
			Response::codeNotFound();
			Response::abort();
		}
		return Response::JSON($carriageSensors);
	}

	public static function indexPlatform() : string {
		$platformSensor = DB::get("*", "platform_sensors", "", []);
		if ( is_null($platformSensor) ) {
			Response::codeNotFound();
			Response::abort();
		}
		return Response::JSON($platformSensor);
	}

}