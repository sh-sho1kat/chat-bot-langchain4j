package com.sho1kat;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface Tutor {

    @SystemMessage("""
            You are Sho1kat, a kind homework helper for students in class 4, 5 and 6
            (around 9 to 12 years old).

            How you talk:
            - Use very simple words and short sentences. Imagine explaining to a younger cousin.
            - Never more than 4 lines at a time, then ask one small question to check they understood.
            - Never give the answer to homework straight away. Give a hint or a first step,
              and only show the full working if the student is still stuck after trying.
            - Use friendly examples from daily life: mangoes, cricket, tiffin break, school bus.
            - Praise effort, not just correct answers.

            Subjects you help with: Mathematics, Science, English, Bangla, Bangladesh and
            world geography, ICT, moral science, drawing ideas.

            If the question is not about school or learning, gently bring them back to studies.
            Answer in Banglish (Bengali words in English letters mixed with English) by default,
            and switch to the language the student writes in.
            """)
    TokenStream chat(@MemoryId String studentId, @UserMessage String message);

    record Quiz(
            @Description("One simple question for a class 4-6 student.") String question,
            @Description("Exactly four short options, only one correct.") List<String> options,
            @Description("Correct option, copied verbatim from options.") String answer,
            @Description("One easy sentence explaining why, as to a child.") String why) {
    }

    @SystemMessage("""
            You are a primary-school exam author for class 4, 5 and 6 students
            (around 9 to 12 years old).
            Write exactly one multiple-choice question.
            Use simple words only. Exactly four short options, exactly one correct,
            and copy the answer verbatim from the options.
            The explanation must be one easy sentence a child can follow.
            No "all of the above".
            """)
    @UserMessage("Write a class {{grade}} question about: {{topic}}")
    Quiz quiz(@V("topic") String topic, @V("grade") int grade);

    class Tools {

        @Tool("List the chapters in a school subject for class 4-6. "
                + "Subjects: math, science, english, bangla, geography, ict.")
        public String syllabus(@P("Subject name") String subject,
                               @P("Class: 4, 5 or 6") int grade) {
            String ch = switch (subject.toLowerCase().trim()) {
                case "math" -> switch (grade) {
                    case 4 -> "Multiplication and division, fractions, decimals, shapes, "
                            + "length and weight, time and money, data handling.";
                    case 5 -> "Large numbers, fractions, decimals, percentage, average, "
                            + "area and perimeter, angles, simple graphs.";
                    default -> "Fractions, decimals, percentage, ratio, simple equation, "
                            + "integers, area, volume, algebraic expression.";
                };
                case "science" -> switch (grade) {
                    case 4 -> "Living and non-living things, plants, animals, our body, "
                            + "food and nutrition, air and water, sound.";
                    case 5 -> "Cells and tissues, plant parts, animal classification, "
                            + "food chains, health, force and energy, light.";
                    default -> "Cell, classification, photosynthesis, human body systems, "
                            + "matter, motion, simple machines, electricity, earth and space.";
                };
                case "english" -> "Nouns, pronouns, verbs, adjectives, articles, preposition, "
                        + "tense (present and past), punctuation, reading comprehension, "
                        + "paragraph writing, letter writing.";
                case "bangla" -> "Parts of speech, sentence making, tense, comprehension, "
                        + "essay writing, letter writing, poem recitation.";
                case "geography" -> "Directions and maps, landforms, rivers and climate, "
                        + "districts of Bangladesh, national symbols, continents and oceans, "
                        + "seasons and natural disasters.";
                case "ict" -> "Parts of a computer, keyboard and mouse, files and folders, "
                        + "paint and word processor, internet safety, e-mail, "
                        + "simple scratch programming.";
                default -> "No syllabus for '" + subject + "'. Subjects: math, science, english, "
                        + "bangla, geography, ict.";
            };
            return "Class " + grade + " " + subject + " chapters: " + ch;
        }

        @Tool("Give the simple meaning of a word or a school term the student is asking about.")
        public String define(@P("The word or term") String word) {
            return switch (word.toLowerCase().trim()) {
                case "noun" -> "Noun: the name of a person, place or thing. "
                        + "Example: Rahim, Dhaka, mango.";
                case "verb" -> "Verb: a word that tells what someone does. Example: eat, run, write.";
                case "adjective" -> "Adjective: a word that describes a noun. "
                        + "Example: a sweet mango, a big river.";
                case "fraction" -> "Fraction: a part of a whole. If a biscuit is cut into 4 equal "
                        + "pieces, taking 1 piece is 1/4.";
                case "percentage" -> "Percentage: an amount out of 100. 50% means 50 out of 100, "
                        + "like half a cake.";
                case "photosynthesis" -> "Photosynthesis: the way green plants make their own food "
                        + "using sunlight, water and air.";
                case "noun definition" -> "A noun names a person, place or thing.";
                default -> "No ready definition for '" + word + "'. Explain it with a simple "
                        + "everyday example a child understands.";
            };
        }
    }
}
