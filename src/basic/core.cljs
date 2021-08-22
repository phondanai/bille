(ns basic.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [clojure.string :refer [split]]))


(def item-v (r/atom "item"))
(def price-v (r/atom 0))
(defonce timer (r/atom (js/Date.)))
(defonce time-updater (js/setInterval #(reset! timer (js/Date.)) 1000))

(defonce items (r/atom (sorted-map)))
(defonce counter (r/atom 0))

(defn add-item [item price]
  (let [id (swap! counter inc)]
    (swap! items assoc id {:id id :item item :price (js/parseInt price)})))


(defn parse-params
  "Parse URL parameters into a hashmap. https://gist.github.com/kordano/56a16e1b28d706557f54"
  []
  (if-let [params-strs (->
                        (-> js/window .-location .-search
                            (js/decodeURI)
                            (split #"\?") last (split #"\&")))]
    (into (sorted-map) (for [[k v] (map #(split % #"=") params-strs) :when (not= v nil)]
                         [[:item k] [:price v]]))))


(defn add-item-params
  "Parse URL parameters into a hashmap. https://gist.github.com/kordano/56a16e1b28d706557f54"
  []
  (if-let [params-strs (->
                        (-> js/window .-location .-search
                            (js/decodeURI)
                            (split #"\?") last (split #"\&")))]
    (for [[k v] (map #(split % #"=") params-strs) :when (not= v nil)]
       (add-item k v))))

(defn item-item []
  (fn [{:keys [id item price]}]
    [:tr
     [:td item]
     [:td price]]))


(defonce init
  (.log js/console (str (add-item-params))))
;; -------------------------
;; Views

(defn item-input []
 [:div
  [:label "Item"]
  [:input {:placeholder "Item"
           :on-change #(reset! item-v (-> % .-target .-value))}]])

(defn price-input []
 [:div
  [:label "Price"]
  [:input {:placeholder "Price"
           :on-change #(reset! price-v (-> % .-target .-value))}]])

(defn show-time []
  [:div {:style {:color "white" :background "grey"}}
   (str "The time is:" (.toTimeString @timer))])

(defn bille []
  (let [all-items (vals @items)]
    [:div
     [:h1 "Bille!!"]
     [:div.pair.row
       [item-input]
       [price-input]]
     [:input {:type "button" :value "add"
              :on-click #(add-item @item-v @price-v)}]
     [:hr]
     [:table
      [:thead
       [:tr
        [:th "item"]
        [:th "price"]]]
      [:tbody
       (for [item all-items]
         ^{:key (:id item)} [item-item item])]
      [:tfoot
       [:tr [:td "TOTAL"] [:td (reduce + (map :price (vals @items)))]]]]
     [show-time]
     [:hr]]))
     ;[:p (js/decodeURI (-> js/window .-location .-search))]]))


(defn home-page []
 (bille))


;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))


(defn init! []
  (mount-root))
