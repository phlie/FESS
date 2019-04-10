;; The name of the project
(def project 'fess)
;; The current version of the project
(def version "0.0.1")

;; Sets the environment variables, for now it simply sets the resource path and the dependency, Clojure
(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure "RELEASE"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-jetty-adapter "1.6.3"]])

;; Simple task options for now, will expand as the project goes along
(task-options!
 pom {:project 'fess
      :version version})

;; Uses the file structure in a directory_name.file_name sort of way, this is required for the 'run' task
(require '[core :as core])

;; Calls the main function from the code in, at the moment, a very simple way
(deftask run
  "Runs the simulator."
  []
  (with-pass-thru _
    (core/-main)))
