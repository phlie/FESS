(ns core                                ; The main namespace
  (:use [ring.adapter.jetty]            ; All of the libraries used
        [ring.middleware.params]
        [ring.middleware.json :refer [wrap-json-response]]
        [ring.util.response])
  (:gen-class))                         ; Generates compiled bytecode for the 'core' namespace

(def frames-sent 10)                    ; The current amount of data to send between exchange
(def frames-per-second 30)              ; The current number of frames of the frontend per second

;; This function is used at the start of the frontend
;; simulator running to reset all the variables
(defn init-vars
  "The variables used in the simulation"
  []
  (def t 0)                             ; 1 Frame, currently set to 15 frames a second
  (def a -9.81)                         ; Meters per second per second
  (def vx 0.0)                         ; The various velocities
  (def vy 10.0)                         ; Meters per second
  (def vz 0.0)
  (def px [])                            ; The various positions
  (def py [])                      ; X Y Z, with Y being straight up
  (def pz [])
  (def x 0)                             ; The current position in all the axeses
  (def y 0)
  (def z 0)
  (def rx [])                            ; The current rotation in the 3 dimensions
  (def ry 0.0)
  (def rz 0.0)
  (def rot-x 0.0))

;; Computes the velocity at every frame of the rocket
;; then in the front end times by a frame delta
(defn velocity
  "Used to determine the velocity of the rocket"
  []
  (def v (+ v (* a (/ t frames-per-second)))) ; The equation of motion for a projectile launched with a starting velocity
  (def t (inc t))                             ; Increments the current time with every frontend frame
  {:velocity v})                                          ; Return the current velocity

(defn position-and-rotation
  "Used to get the final position vector and rotation vector"
  []
  (def px [])                           ; Initialize all the arrays to empty
  (def py [])
  (def pz [])
  (def rx [])
  (dotimes [n frames-sent]              ; Loop through the amount of data to send
    (def vy (+ vy (* a (/ 1 frames-per-second)))) ; Calculate the velocity
    (def x (+ x (* vx (/ 1 frames-per-second))))  ; Calculate the new position
    (def y (+ y (* vy (/ 1 frames-per-second))))
    (def z (+ z (* vz (/ 1 frames-per-second))))
    ;; (println "Position: " py "Velocity: " v)
    (when (< vy 0.0)
      (def rot-x (min Math/PI (+ rot-x 0.05))))
    (println "Position-y" y)
    (def px (conj px x))                ; Append the array with the newest value
    (def py (conj py y))
    (def pz (conj pz z))
    (def rx (conj rx rot-x))
    ;; (println y)
    ;; (println py)     ; Used for debugging
    (def t (inc t)))    ; Increment the time by 1
  (println "Velocity: " vy "Position-Y: " y)
  (println rx)
  {:px px :py py :pz pz                 ; Send all the information the Frontend expects
   :rx rx :ry ry :rz rz})



;; Used as to receive and send data between the frontend and the backend
(defn handler [{{reset "reset"} :params}]
  "The main server handler"
  ;; (println reset)
  (when (= reset "y") (init-vars))
                                        ; If the frontend sents a reset signal, reset the variables
  (-> (response (position-and-rotation)) ; Send, for now, the velocity back to the frontend
      ;; (content-type "text/html")     ; Before it was sending text
      (content-type "application/json") ; It is currently sending JSON
      ;; Needs to work on the same local machine, port 3000
      (header "Access-Control-Allow-Origin" "http://localhost:3000")))

;; Wraps the handlers key and values as JSON data, and then wraps the parameters such as content-type
(def app
  (-> (wrap-json-response handler) wrap-params))


;; The main function for the project, called with 'boot run'
(defn -main
  "This is the entry point"
  []
  (println "Hello Earth")
  (init-vars)                     ; Just in case the frontend doesn't
  ;; Can be stopped by running (.stop my-server)
  ;; This function starts the server and makes it keep running without shutting down
  (def my-server (run-jetty app {:port 3000 :join? true})))

