(ns txture.mvc.view
  (:use 
     txture.config
     hiccup.core
     hiccup.page-helpers))

;; fns which return entire HTML docs
;; ---------------------------------

(defn- html-doc [title head body] 
  (println title)
  (println head)
  (println body)
  (html (doctype :html5)
    [:html 
     head
    [:body body]]))

;; fns which return <head>
;; ---------------------

(defn- generic-head
  [title & other-tags]
  [:head 
   [:title title]
   [:meta {:name "author" :content *author*}]
   [:meta {:http-equiv "Content-Type" :content "text/html" :charset "UTF-8"}]
   (include-css *css-loc*)
   other-tags])

(defn- root-head
  []
  (generic-head
    *text-title*
    [:meta {:name "keywords" :content *keywords*}]
    [:meta {:name "description" :content *description*}]))

(defn- post-head
  [post]
  (generic-head
    (post :title)
    [:meta {:name "keywords" :content (post :labels-str)}]
    [:meta {:name "description" :content *description*}]))

;; fns which return div#page
;; -------------------------

(defn- wrap-page-with-header
  "Return page in div#page".
  [header content]
  [:div#page
   header
   content])

(defn- page-with-title-header
  "Surround body with title/subtitle."
  ([content] ; home page
   (wrap-page-with-header
     [:div#page-header
      [:div#title 
       [:span.slash0 "/ "] 
       [:a {:href "/"} *title*]
       [:span.slash1 " / "]
       [:span.leaf-crumb "posts"]]
      [:div#subtitle *subtitle*]]
     content))

  ([content title] ; not home
   (wrap-page-with-header [:div#page-header title [:div#subtitle ""]] 
                          content)))

;; fns which return elements within div#page
;; -----------------------------------------

(defn- breadcrumb-header-post
  "Given a post, generate breadcrumb-style title"
  [post]
  [:div#smalltitle 
   [:span.slash0 "/ "] 
   [:a {:href "/"} [:span.sitetitle *title*]]
   [:span.slash1 " / "]
   [:span.child-dir "post"]
   [:span.slash2 " / "]
   [:span.leaf-crumb (post :short-name)]])

(defn- wrap-post
  "Wraps and displays post with title, post-date, etc."
  [post]
  (println post)
  [:div.post
   (*before-post* post)
   (*post-body* post)
   (*after-post* post)])
   
(defn- put-in-main
  "Put content in main."
  [content]
  [:div#main content])

(defn- add-sidebar
  "Adds a sidebar to the left of the content div."
  [html-content]
  [:div html-content 
   [:div#sidebar (*gen-sidebar*)]])

(defn- add-footer
  [html-content]
  [:div html-content [:div#footer (*gen-footer*)]])

;; fns to be used in `mvc.controller`
;; -----------------------------------

(defn show-posts
  [posts] 
  (let [wrapped-posts (for [x posts] (wrap-post x))
        main (put-in-main wrapped-posts)
        all-content (add-footer (add-sidebar main))]
    (html-doc 
      *text-title*
      (root-head)
      (page-with-title-header all-content))))

(defn show-post
  [post]
  (let [header (breadcrumb-header-post post)
        main (put-in-main (wrap-post post))
        all-content (add-footer (add-sidebar main))]
    (html-doc
      (post :title)
      (post-head post)
      (page-with-title-header all-content header))))


