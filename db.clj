(ns db
    (:require [clojure.string :as str])
)


(defn info [infoStr]
    (map #(str/split % #"\|") (str/split-lines infoStr))
)

(defn loadStudData [studfile]
    (def studDB (info (slurp studfile)))

)

(defn loadCourseData [coursefile]
    (def courseDB (info (slurp coursefile)))
)

(defn loadGradeData [gradefile]
    (def gradeDB (info (slurp gradefile)))
)