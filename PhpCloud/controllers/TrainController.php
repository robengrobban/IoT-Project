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

 }