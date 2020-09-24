(ns power-turtle.api.specter
   (:require
     #?(:cljs [power-turtle.view.toolbar :as toolbar])
     [com.rpl.specter :as sp]))

; (def ^:macro select sp/select)

; (def FIRST sp/FIRST)

(defn get-last [structure]
  (sp/select sp/LAST structure))