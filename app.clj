(ns app
    (:require [menu] [db])
)


; load data at beginning
(defn -main [& args]
    
    (def studDB (db/loadStudData "studs.txt"))
    (def courseDB (db/loadCourseData "courses.txt"))
    (def gradeDB (db/loadGradeData "grades.txt"))

; then start menu
   (menu/menuOp)
)

(-main)