# chat-bot-langchain4j

A homework helper chatbot for **class 4, 5 and 6** students (age ~9-12), built with
[LangChain4j](https://docs.langchain4j.dev) 1.23 and Google Gemini.

Helps with Mathematics, Science, English, Bangla, Geography and ICT. Answers in
Banglish by default, uses short sentences and everyday examples, and gives hints
instead of handing over homework answers.

## Run

```bash
export GEMINI_API_KEY="..."
mvn -q compile
mvn -q exec:java -Dexec.mainClass=com.sho1kat.App
```

## How it works

```
App.java        terminal loop: asks the class, streams answers, runs quizzes
Tutor.java      the AI service: prompts, tools, and the Quiz record
ChatHelper.java builds the Gemini models and wires the service
```

Three LangChain4j features are used:

- **Chat memory** - `ChatMemoryProvider` keeps a 20-message window per student, so
  follow-up questions like "abar bujhaye dao" stay in context.
- **Streaming** - `chat()` returns a `TokenStream`, so words appear as they are generated.
- **Agentic tools** - `Tutor.Tools.syllabus(subject, grade)` and `define(word)` are
  called by the model when it needs real chapter lists or word meanings.
- **Structured output** - `Tutor.quiz()` returns a `Quiz` record, so the model must
  reply as JSON matching that schema instead of free text.

## Commands

```
/quiz <topic>    one multiple-choice question for your class
/reset           clear the chat memory
exit             quit
```

## Notes

- The syllabus and glossary in `Tutor.Tools` are fixed lists for classes 4-6. Swap them
  for a real curriculum database to make it school-specific.
- The prompt tells the model to hint first and only give full working when the student
  is still stuck.
