(ns menu
    (:require [db])
    (:require [clojure.string :as str])
    )

(defn displayCourses []
    (doseq [courses db/courseDB]
        (println courses)
    )
)

(defn displayStudents []
    (doseq [students db/studDB]
        (println students)
    )
)

(defn displayGrades []
    (doseq [grades db/gradeDB]
        (println grades)
    )
)

(defn studentRecord [id]
    (let[
        filtered_gradeDB (filter #(= id (first %)) db/gradeDB)  ;filtered grade db
        filtered_studDB (filter #(= id (first %)) db/studDB)
        [[_ student-name _]] filtered_studDB    ;since the output of filetered_studDB is a nested data structure, we use [[ ]] to destructure it
        ]

        (if (not-empty filtered_studDB)  ;if we can find corresponds student in db
            (do
                (println id student-name) ; print id and name

                (doseq [grade filtered_gradeDB]
                    (let [courseID (second grade)
                          courseInfo (filter #(= courseID (first %)) db/courseDB)
                          [[_ cournam courno _ desc]] courseInfo
                          ]
                          (println cournam courno desc (get grade 2) (get grade 3))
                    )
                )
            )
            (do 
                (println "Student Not Found!"))
        )
    ) 
)

(defn calculateGPA [id]
    (let [
        gradePointMap {"A+" 4.3, "A" 4, "A-" 3.7, "B+" 3.3, "B" 3, "B-" 2.7, "C+" 2.3, "C" 2, "C-" 1.7, "D+" 1.3, "D" 1, "D-" 0.7, "F" 0}
        filtered_gradeDB (filter #(= id (first %)) db/gradeDB)  ;([2408011 1 2023-S A+] [2408011 2 2023-S A] [2408011 3 2023-S B+])
        ]

        (loop [gradePtSum 0
               creditsSum 0
               gradeList filtered_gradeDB]
               (let [
                    gradeInfo (first gradeList) ;[2408011 1 2023-S A+]
                    courseID (second gradeInfo) ;(1)
                    letterGrade (get gradeInfo 3)
                    numGrade (get gradePointMap letterGrade)
                    courseInfo (first (filter #(= courseID (first %)) db/courseDB)) ;[1|COMP|348|3|Principles of Programming Languages]
               ]
                ;base case
                (if (empty? gradeList)
                    (if (not= creditsSum 0)
                        (println "GPA compute result is: "id (format "%.2f" (/ gradePtSum creditsSum)))
                        0
                    )
                )
               (if courseInfo  ;calculate creditSum here
                    (let [ 
                        credit (get courseInfo 3)
                    ]
                    (recur (+ gradePtSum (* numGrade (Integer/parseInt credit))) (+ creditsSum (Integer/parseInt credit)) (rest gradeList))
                    )           
               )              
               )   
        )     
    )
)

(defn courseAvg[courseName courseNo semester]
    (let[
        gradePointMap {"A+" 4.3, "A" 4, "A-" 3.7, "B+" 3.3, "B" 3, "B-" 2.7, "C+" 2.3, "C" 2, "C-" 1.7, "D+" 1.3, "D" 1, "D-" 0.7, "F" 0}
        filteredList (filter #(= courseName (get % 1)) db/courseDB)
        filteredList2(filter #(= courseNo (get % 2)) filteredList) ;([1 COMP 348 3 Principles of Programming Languages])
        ;filteredList (filter #(and (= courseName (get % 1) (= courseNo (get % 2)))) db/courseDB)
        [[code _ _ _ _]] filteredList2

        filteredGrade (filter #(= code (get % 1)) db/gradeDB)
        filteredGrade2 (filter #(= semester (get % 2)) filteredGrade)   
    ]
        (if (not-empty filteredGrade2)
            (do
                (loop [count 0
                       gradeSum 0
                       grade filteredGrade2]

                    (let [
                        gradeInfo (first grade) ;[2408011 1 2023-S A+]
                        letterGrade (get gradeInfo 3)
                        numGrade(get gradePointMap letterGrade)
                    ]
                    ;base case
                    (if (empty? grade)
                        (if (> count 0)
                            (println courseName courseNo semester (/ gradeSum count))
                        )   
                    )
                    (if numGrade
                        (recur (+ count 1) (+ gradeSum numGrade) (rest grade))
                    )   
                    )               
                )
            )
            (do
                (print "No Course Found!")
            )
        )
    )
)

(defn menuOp [] 
    (loop[]
        (println "\n*** SIS Menu ***")
        (println "------------------")
        (println "1. Display Courses")
        (println "2. Display Students")
        (println "3. Display Grades")
        (println "4. Display Student Record")
        (println "5. Calculate GPA")
        (println "6. Course Average")
        (println "7. Exit")
        (println "Enter an option?")

        (flush)
        (def option (read-line))

        (cond
            (= option "1") 
                (do 
                    (displayCourses)
                    (recur))

            (= option "2") 
                (do 
                    (displayStudents)
                    (recur))
            
            (= option "3") 
                (do 
                    (displayGrades)
                    (recur))

            (= option "4") 
                (do 
                    (println "Please enter student's id: ")
                    (flush)
                    (def id (read-line))
                    
                    (studentRecord id)
                    (recur))

            (= option "5") 
                (do 
                    (println "Please enter student's id: ")
                    (flush)
                    (def id (read-line))
                    
                    (calculateGPA id)
                    (recur))

            (= option "6") 
                (do 
                    (println "Please enter course name:(eg. COMP) ")
                    (flush)
                    (def courseName (str/upper-case (read-line)))
                    (println "Please enter course No: ")
                    (flush)
                    (def courseNo (read-line))
                    (println "Please enter semester:(eg. 2023-S)")
                    (flush)
                    (def semester (str/upper-case (read-line)))

                    (courseAvg courseName courseNo semester)
                    (recur))

            (= option "7") 
                (do 
                    (println "Exiting... Goodbye!")
                    (System/exit 0)
                    )

            :else   (do 
                        (println "Invalid input, please try again!")
                        (recur))
        )      
))