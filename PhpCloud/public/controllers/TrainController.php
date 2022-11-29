<?php
/**
 * @author Robert Englund
 */

 class TrainController {

    public static function index() : string {
        $trains = DB::get("*", "trains", "", []);
        if ( is_null($trains) ) {
            Response::codeNotFound();
            Response::abort();
        }

        return Response::JSON($trains);
    }

	public static function get() : string {
		if ( !Request::params()->nonempty("id") ) {
			Response::codeBadRequest();
			Response::abort();
		}

		$train = DB::get("*", "trains", "id = ?", [Request::params()->get("id")]);
		if ( is_null($train) || empty($train) ){
			Response::codeNotFound();
			Response::abort();
		}

		return Response::JSON($train);
	}

	public static function next() : string {
		if ( !Request::params()->nonempty("platform") ) {
			Response::codeBadRequest();
			Response::abort();
		}

		$train = DB::get("*", "trains", "id = ?", [1]);
		if ( is_null($train) || empty($train) ) {
			Response::codeNotFound();
			Response::abort();
		}

		return Response::JSON($train);
	}

 }