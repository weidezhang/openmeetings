<?php

require_once('RestService.php');

class TeamBoxRestService extends RestService {
    
    private $restApiUrl;
    private $accessToken;

    public function TeamBoxRestService($accessToken) {
        parent::RestService('TeamBox');
        
        global $CFG;
        $this->restApiUrl = $CFG[teambox_api_url];
        $this->accessToken = $accessToken;
    }
    
    public function isValidAccessToken() {
        $url = $this->restApiUrl . 'account';
        $parameters = 'access_token=' . $this->accessToken;
        $response = $this->call($url, $parameters, 'GET', false);
        if (false == $response) {
            return false;
        }
        
        return true;
    }
    
    public function getAccountInfo() {
        $url = $this->restApiUrl . 'account';
        $parameters = 'access_token=' . $this->accessToken;
        $response = $this->call($url, $parameters, 'GET');
        $data = $this->getJsonSingleData($response);

        return $data;
    }
    
    public function getUserProjects() {
        $url = $this->restApiUrl . 'projects';
        $parameters = 'access_token=' . $this->accessToken;
        $response = $this->call($url, $parameters, 'GET');
        $data = $this->getJsonMultiData($response);
        
        return $data;
    }
    
    private function getJsonSingleData($response) {
        if (!($content = strstr($response, '{'))) {
            $content = null;
        }
        
        $data = json_decode($content);
        
        return $data;
    }
    
    private function getJsonMultiData($response) {
        $singlePos = stripos($response, '{');
        $multiPos = stripos($response, '[');
        
        $multy = false;
        if (-1 != $multiPos) {
            if (-1 != $singlePos) {
                $multy = ($multiPos < $singlePos);
            } else {
                $multy = true;
            }
        }
        
        if ($multy) {
            $content = strstr($response, '[');
        } else {
            $content = strstr($response, '{');
        }

        if (!$content) {
            return null;
        }

        $data = json_decode($content);
        if (!$multy) {
            $data = array(0 => $data);
        }
    
        return $data;
    }

}

?>
