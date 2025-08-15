import java.util.*;
public class Quizapp {
    static class Question {
        private final int id;
        private final String text;
        private final List<String> options; 
        private final int correctIndex;     
        private final String topic;         
        private final String explanation;

        Question(int id, String text, List<String> options, int correctIndex,
                 String topic, String explanation) {
            this.id = id;
            this.text = text;
            this.options = new ArrayList<>(options);
            this.correctIndex = correctIndex;
            this.topic = topic;
            this.explanation = explanation;
        }

        int getId() { return id; }
        String getText() { return text; }
        List<String> getOptions() { return Collections.unmodifiableList(options); }
        int getCorrectIndex() { return correctIndex; }
        String getTopic() { return topic; }
        String getExplanation() { return explanation; }

     
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Question)) return false;
            Question q = (Question) o;
            return id == q.id;
        }
        @Override public int hashCode() { return Objects.hash(id); }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        List<Question> questions = buildQuestionBank();

        
        Collections.shuffle(questions);

        System.out.println("==== Online Quiz App ====");
        System.out.println("Answer with A/B/C/D or 1/2/3/4.");
        System.out.println("Press ENTER to start...");
        sc.nextLine();

        Map<String, Integer> correctPerTopic = new HashMap<>();
        Map<String, Integer> totalPerTopic   = new HashMap<>();
        Map<Question, Integer> userAnswers   = new LinkedHashMap<>();

        
        int score = 0;
        Iterator<Question> it = questions.iterator();
        int qNum = 1;
        while (it.hasNext()) {
            Question q = it.next();
            System.out.println();
            System.out.println("Q" + qNum + ". [" + q.getTopic() + "] " + q.getText());

            char label = 'A';
            for (String opt : q.getOptions()) {
                System.out.println("  " + label + ") " + opt);
                label++;
            }

            int userIndex = readOptionIndex(sc); // 0..3
            userAnswers.put(q, userIndex);

            totalPerTopic.merge(q.getTopic(), 1, Integer::sum);

            if (userIndex == q.getCorrectIndex()) {
                score++;
                correctPerTopic.merge(q.getTopic(), 1, Integer::sum);
                System.out.println("✅ Correct!");
            } else {
                System.out.println("❌ Incorrect.");
                System.out.println("   Correct answer: " +
                        letterForIndex(q.getCorrectIndex()) + ") " +
                        q.getOptions().get(q.getCorrectIndex()));
            }

            qNum++;
        }

        System.out.println("\n===== Results =====");
        System.out.println("Score: " + score + " / " + questions.size());
        double percent = (score * 100.0) / questions.size();
        System.out.printf("Percentage: %.2f%%\n", percent);

        System.out.println("\n-- Topic-wise Performance --");
        Set<String> allTopics = new TreeSet<>();
        allTopics.addAll(totalPerTopic.keySet());
        allTopics.addAll(correctPerTopic.keySet());
        for (String topic : allTopics) {
            int total = totalPerTopic.getOrDefault(topic, 0);
            int corr  = correctPerTopic.getOrDefault(topic, 0);
            double pct = total == 0 ? 0.0 : (corr * 100.0) / total;
            System.out.printf("%s: %d/%d (%.1f%%)\n", topic, corr, total, pct);
        }

        List<Question> wrong = new ArrayList<>();
        for (Map.Entry<Question, Integer> e : userAnswers.entrySet()) {
            if (!Objects.equals(e.getKey().getCorrectIndex(), e.getValue())) {
                wrong.add(e.getKey());
            }
        }
        if (!wrong.isEmpty()) {
            wrong.sort(Comparator.comparing(Question::getTopic)
                                 .thenComparingInt(Question::getId));
            System.out.println("\n-- Review (Incorrect Answers, sorted by Topic then Id) --");
            for (Question q : wrong) {
                int picked = userAnswers.get(q);
                System.out.println("Q" + q.getId() + " [" + q.getTopic() + "]: " + q.getText());
                System.out.println("   Your answer : " + letterForIndex(picked) + ") " +
                        q.getOptions().get(picked));
                System.out.println("   Correct     : " + letterForIndex(q.getCorrectIndex()) + ") " +
                        q.getOptions().get(q.getCorrectIndex()));
                System.out.println("   Why? " + q.getExplanation());
            }
        } else {
            System.out.println("\nPerfect! No incorrect answers to review.");
        }

        List<Question> key = new ArrayList<>(questions);
        key.sort(Comparator.comparingInt(Question::getId));
        System.out.println("\n-- Answer Key --");
        for (Question q : key) {
            System.out.println("Q" + q.getId() + ": " +
                    letterForIndex(q.getCorrectIndex()) + ") " +
                    q.getOptions().get(q.getCorrectIndex()) + "  [" + q.getTopic() + "]");
        }

        System.out.println("\nThanks for playing!");
        sc.close();
    }

    private static int readOptionIndex(Scanner sc) {
        while (true) {
            System.out.print("Your choice (A-D or 1-4): ");
            String raw = sc.nextLine().trim();
            if (raw.isEmpty()) continue;

            char c = Character.toUpperCase(raw.charAt(0));
            switch (c) {
                case 'A': case '1': return 0;
                case 'B': case '2': return 1;
                case 'C': case '3': return 2;
                case 'D': case '4': return 3;
                default:
                    System.out.println("Invalid input. Please enter A, B, C, D or 1, 2, 3, 4.");
            }
        }
    }

    private static char letterForIndex(int idx) {
        return (char) ('A' + idx);
    }

    private static List<Question> buildQuestionBank() {
        List<Question> list = new ArrayList<>();
        int id = 1;

        list.add(new Question(
                id++,
                "What are Java loops?",
                Arrays.asList(
                        "Constructs that repeat a block of code while a condition holds",
                        "Classes that store key-value pairs",
                        "Predefined methods in java.lang.Math",
                        "A way to define packages"
                ),
                0,
                "Control Flow",
                "Loops (for/while/do-while) execute code repeatedly based on a condition."
        ));

        list.add(new Question(
                id++,
                "What is the enhanced for-loop?",
                Arrays.asList(
                        "A loop that runs only once per method",
                        "A foreach-style loop to iterate over arrays/collections without indices",
                        "A loop that sorts a list automatically",
                        "A loop that only iterates Maps"
                ),
                1,
                "Control Flow",
                "The enhanced for-loop (for-each) iterates directly over elements in arrays/collections."
        ));

        list.add(new Question(
                id++,
                "How do you typically handle multiple user inputs in a console app?",
                Arrays.asList(
                        "Using Scanner in a loop with validation",
                        "Only using command-line arguments",
                        "By compiling twice",
                        "By using synchronized blocks"
                ),
                0,
                "IO & Basics",
                "Use Scanner, read each token/line, and validate inside a loop."
        ));

        list.add(new Question(
                id++,
                "Switch-case vs if-else: which is generally preferred for discrete value checks (like menu choices)?",
                Arrays.asList(
                        "switch-case is often clearer/faster for discrete choices",
                        "if-else is always faster",
                        "Both are illegal in Java",
                        "Neither can handle chars"
                ),
                0,
                "Control Flow",
                "Switch is clearer for many fixed alternatives (e.g., 'A','B','C'), while if-else fits ranges/complex conditions."
        ));

        list.add(new Question(
                id++,
                "What are Collections in Java?",
                Arrays.asList(
                        "A framework of classes/interfaces for data structures like List/Set/Map",
                        "Only arrays and strings",
                        "Only file IO utilities",
                        "Only GUI components"
                ),
                0,
                "Collections",
                "Java Collections Framework provides List, Set, Queue, Map and related utilities."
        ));

        list.add(new Question(
                id++,
                "What is an ArrayList?",
                Arrays.asList(
                        "A resizable array implementation of the List interface",
                        "A fixed-size array type",
                        "A class that guarantees uniqueness like Set",
                        "A linked list node"
                ),
                0,
                "Collections",
                "ArrayList stores elements in a dynamic array; it allows random access and resizes automatically."
        ));

    
        list.add(new Question(
                id++,
                "How to iterate using an Iterator?",
                Arrays.asList(
                        "Use hasNext() and next() to traverse elements sequentially",
                        "Call get(i) inside a while loop always",
                        "Use a constructor with Iterator",
                        "Iterators are only for Maps"
                ),
                0,
                "Collections",
                "Iterator provides a uniform way to traverse a collection via hasNext()/next()."
        ));

        list.add(new Question(
                id++,
                "What is a Map?",
                Arrays.asList(
                        "An interface for key-value pairs with unique keys",
                        "A type of for-loop",
                        "A sorting algorithm",
                        "A special array for primitives"
                ),
                0,
                "Collections",
                "Map stores associations from keys to values; keys are unique."
        ));

        list.add(new Question(
                id++,
                "How to sort a List in Java?",
                Arrays.asList(
                        "Use Collections.sort(list) or list.sort(Comparator)",
                        "Sort happens automatically on print",
                        "Only by converting to array",
                        "Java cannot sort lists"
                ),
                0,
                "Collections",
                "Use Collections.sort(list) or list.sort with a Comparator for custom order."
        ));

        list.add(new Question(
                id++,
                "How to shuffle elements in a List?",
                Arrays.asList(
                        "Use Collections.shuffle(list)",
                        "Shuffling is not supported",
                        "Call list.sort randomly",
                        "Use Arrays.copyOf"
                ),
                0,
                "Collections",
                "Collections.shuffle(list) randomizes element order."
        ));

        return list;
    }
}
