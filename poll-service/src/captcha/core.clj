(ns captcha.core
  (:require [com.stuartsierra.component :as component]
            [clj-http.conn-mgr :as conn]
            [clj-http.client :as http]))

(defprotocol CaptchaService
  (verify? [this response]))

(defrecord Captcha [url secret manager]
  component/Lifecycle
  
  (start [this]
    (let [manager (conn/make-reusable-conn-manager {})]
      (assoc this :manager manager)))
  
  (stop [this]
    (conn/shutdown-manager manager)
    (assoc this :manager nil))
  
  CaptchaService
  
  (verify? [this response]
    (let [params {:secret secret :response response}]
      (-> (http/post url {:connection-manager manager
                          :form-params params
                          :as :json})
          (get-in [:body :success])))))

(defn new-captcha [config]
  (map->Captcha config))
