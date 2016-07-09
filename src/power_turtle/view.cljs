(ns power-turtle.view
  (:require
    [power-turtle.power :as power]
    [power-turtle.repl :as repl]
    [clojure-turtle.core :as turtle]
    [quil.core :as quil]
    [reanimated.core :as anim]
    [reagent.core :as reagent]
    [reagent.dom :refer [dom-node]]
    [re-frame.core :refer [subscribe dispatch]]))

(defn action [k f]
  [:button
   {:on-click #(f)}
   (name k)])

(defn toolbar []
  [:div
   (for [[k f] @power/actions]
     ^{:key k}
     [action k f])])

(defn browser-map [& ss]
  (zipmap
    [:-ms-transform :-moz-transform :-webkit-transform :transform]
    (repeat (apply str ss))))

(def flip (reagent/atom 90))

(defn flipper [tag attrs & children]
  (let [flip-spring (anim/spring flip)]
    (swap! flip -)
    (fn a-flipper [tag attrs]
      [:div
       (into
         [tag
          (merge-with
            merge
            {:style (browser-map "rotateY(" (+ 90 @flip-spring) "deg)")}
            attrs)]
         children)])))

(defn turtle-canvas []
  (reagent/create-class
    {:display-name "turtle-canvas"
     :reagent-render
     (fn []
       [:canvas#turtle-canvas])
     :component-did-mount
     (fn [this]
       (quil/sketch
         :host (dom-node this)
         :size [640 600]
         :setup (fn []
                  (turtle/setup)
                  (power/init))
         :draw turtle/draw))}))

(defn help-tips []
  (let [current-langugage (subscribe [:current-language])]
    (fn a-help-tips []
      [:div
       (into
         [:div]
         (for [language (sort (keys repl/languages))]
           [:button
            {:class (when (= @current-langugage language)
                      "active")
             :on-click
             (fn language-click [e]
               (dispatch [:current-language language]))}
            language]))
       (into
         [:small]
         (interpose
           " · "
           (sort
             (for [[ns translations] (repl/languages @current-langugage)
                   [sym translation] translations]
               [:span
                ;; TODO: figure out how to capture doc
                ;;{:title (with-out-str (cljs.repl/print-doc (meta (var sym))))}
                translation]))))])))

(defn page []
  [:div
   [flipper :img {:src "clojure_logo.png" :style {:float "left"}}]
   [flipper :img {:src "turtle.jpg" :style {:float "right"}}]
   [:h1 "Power Turtle"]
   [toolbar]
   @power/ui
   [:br]
   [:div#main
    [turtle-canvas]
    [repl/repl]]
   [help-tips]])
