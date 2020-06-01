(ns rid3.container-with-data
  (:require
   [cljsjs.d3]
   [rid3.util :as util]))


(defn piece-did-mount [piece opts prev-classes]
  (let [{:keys [id
                ratom]}             opts
        {:keys [class
                did-mount]
         :or   {did-mount (fn [node ratom]
                            node)}} piece
        node                        (js/d3.select (util/node-selector id prev-classes))
        gup?     (get piece :gup)]
    (-> node
        (.append "g")
        (.attr "class" class)
        (did-mount ratom)
        )
    (if gup?
      (do
        ;; enter needs to be after update
        (data/gup-data-exit piece opts prev-classes)
        (data/gup-data-update piece opts prev-classes)
        (data/gup-data-enter-init piece opts prev-classes))
      (do
        ;; update needs to be after enter
        (data/data-enter piece opts prev-classes)
        (data/did-mount-data-update piece opts prev-classes)
        (data/data-exit piece opts prev-classes)))
    ))


(defn piece-did-update [piece opts prev-classes]
  (let [{:keys [id
                ratom]}      opts
        {:keys [class
                did-mount
                did-update]} piece
        did-update           (or did-update
                                 did-mount ;; sane-fallback
                                 (fn [node ratom]
                                   node))
        node                 (js/d3.select (str (util/node-selector id prev-classes)
                                                " ." class))
        gup? (get piece :gup)]
    (did-update node ratom)
    (if gup?
      (do
        ;; enter needs to be after update
        (data/gup-data-exit piece opts prev-classes)
        (data/gup-data-update piece opts prev-classes)
        (data/gup-data-enter piece opts prev-classes))
      (do
        ;; update needs to be after enter
        (data/data-enter piece opts prev-classes)
        (data/did-update-data-update piece opts prev-classes)
        (data/data-exit piece opts prev-classes)))
    ))
