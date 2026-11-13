package com.sho1kat;

import dev.langchain4j.service.TokenStream;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class App {

    private static final String STUDENT = "student-1";

    public static void main(String[] args) {
        Tutor tutor;
        try {
            tutor = ChatHelper.tutor();
        } catch (RuntimeException e) {
            IO.println("Startup failed: " + e.getMessage());
            return;
        }

        int grade = Integer.parseInt(IO.readln("Which class are you in? (4, 5 or 6): ").trim());
        if (grade < 4 || grade > 6) {
            grade = 5;
        }

        IO.println("--------------------------------");
        IO.println("Hi! I am Sho1kat, your study helper for Class " + grade + ".");
        IO.println("Ask me anything about math, science, english, bangla, geography or ICT.");
        IO.println("Type /quiz <topic> to test yourself, or exit to stop.");
        IO.println();

        while (true) {
            String prompt = IO.readln("You: ");
            if (prompt == null) {
                break;
            }
            prompt = prompt.trim();
            if (prompt.isEmpty()) {
                continue;
            }
            if (prompt.equalsIgnoreCase("exit") || prompt.equalsIgnoreCase("/exit")) {
                IO.println("Bye! Keep reading, tumi parbe!");
                break;
            }

            try {
                if (prompt.startsWith("/quiz")) {
                    runQuiz(tutor, prompt.substring("/quiz".length()).trim(), grade);
                } else if (prompt.equalsIgnoreCase("/reset")) {
                    ChatHelper.forget();
                    IO.println("Bot: thik ache, notun kore shuru kori.");
                } else {
                    stream(tutor, prompt, grade);
                }
            } catch (Exception e) {
                IO.println();
                IO.println("[error] " + e.getMessage());
            }
            IO.println();
        }
    }

    private static void stream(Tutor tutor, String prompt, int grade) {
        CompletableFuture<Void> done = new CompletableFuture<>();
        IO.print("Bot: ");

        TokenStream stream = tutor.chat(STUDENT, "I am in class " + grade + ". " + prompt);
        stream
                .onPartialResponse(IO::print)
                .onCompleteResponse(r -> done.complete(null))
                .onError(done::completeExceptionally)
                .start();

        done.join();
    }

    private static void runQuiz(Tutor tutor, String topic, int grade) {
        if (topic.isEmpty()) {
            IO.println("Bot: /quiz needs a topic, like /quiz fractions in math");
            return;
        }

        Tutor.Quiz quiz = tutor.quiz(topic, grade);
        if (quiz == null) {
            IO.println("Bot: question banate parlam na. Try another topic.");
            return;
        }

        IO.println("Bot: Class " + grade + " quiz - " + topic);
        IO.println();
        IO.println(quiz.question());
        IO.println();

        List<String> options = quiz.options() == null ? List.of() : quiz.options();
        for (int i = 0; i < options.size(); i++) {
            IO.println("  " + (char) ('A' + i) + ") " + options.get(i));
        }

        IO.println();
        String answer = IO.readln("Your answer (A-D, press Enter to see it): ");
        answer = answer == null ? "" : answer.trim().toUpperCase();

        if (!answer.isEmpty()) {
            int index = "ABCD".indexOf(answer.charAt(0));
            String picked = (index >= 0 && index < options.size()) ? options.get(index) : null;
            if (picked != null && picked.equals(quiz.answer())) {
                IO.println("Correct! Shundor hoyeche.");
            } else if (picked != null) {
                IO.println("Not right, but good try.");
            }
        }

        IO.println("Answer: " + quiz.answer());
        IO.println("Why: " + quiz.why());
    }
}
