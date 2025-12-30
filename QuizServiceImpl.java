package com.server;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Pattern;

public class QuizServiceImpl extends UnicastRemoteObject implements QuizService {

    private List<QuizResult> results = new ArrayList<>();

    public QuizServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public List<Course> getCourses() throws RemoteException {
        return DataStore.loadCourses();
    }

    @Override
    public void addCourse(String courseName) throws RemoteException {
        DataStore.addCourse(courseName);
    }

    @Override
    public void deleteCourse(String courseName) throws RemoteException {
        DataStore.removeCourse(courseName);
    }

    @Override
    public void addFileToCourse(String courseName, String filePath) throws RemoteException {
        DataStore.addFileToCourse(courseName, filePath);
    }

    @Override
    public void deleteFileFromCourse(String courseName, String filePath) throws RemoteException {
        DataStore.deleteFileFromCourse(courseName, filePath);
    }

    @Override
    public List<Question> getQuestions(String filePath, String questionType, int count) throws RemoteException {
        String rawText = extractTextFromFile(new File(filePath));
        List<Question> allGeneratedQuestions = generateQuestionsFromText(rawText, questionType);
        Collections.shuffle(allGeneratedQuestions);
        
        int finalCount = Math.min(count, allGeneratedQuestions.size());
        return new ArrayList<>(allGeneratedQuestions.subList(0, finalCount));
    }

    @Override
    public void saveResult(QuizResult result) throws RemoteException {
        results.add(result);
        System.out.println("Saved result: " + result.getUsername() + " - Score: " + result.getScore());
    }

    private String extractTextFromFile(File file) {
        String fileName = file.getName().toLowerCase();
        StringBuilder text = new StringBuilder();
        try {
            if (fileName.endsWith(".txt")) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) text.append(line).append(" ");
                }
            } else if (fileName.endsWith(".docx")) {
                try (FileInputStream fis = new FileInputStream(file); XWPFDocument document = new XWPFDocument(fis); XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    text.append(extractor.getText()).append(" ");
                }
            } else if (fileName.endsWith(".pptx")) {
                try (FileInputStream fis = new FileInputStream(file); XMLSlideShow ppt = new XMLSlideShow(fis)) {
                    for (XSLFSlide slide : ppt.getSlides()) {
                        for (XSLFShape shape : slide.getShapes()) {
                            if (shape instanceof XSLFTextShape) text.append(((XSLFTextShape) shape).getText()).append(" ");
                        }
                    }
                }
            } else if (fileName.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(file)) {
                    text.append(new PDFTextStripper().getText(document)).append(" ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString().replaceAll("\\s+", " ").trim();
    }

    private List<Question> generateQuestionsFromText(String text, String questionType) {
        List<Question> questions = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(text);
        int start = iterator.first();
        List<String> sentences = new ArrayList<>();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(text.substring(start, end).trim());
        }
        
        Random random = new Random();

        for (String sentence : sentences) {
            if (sentence.length() < 20 || sentence.length() > 300) continue;

            Question q = null;
            switch (questionType) {
                case "Fill in the Blank": q = generateFillInBlank(sentence); break;
                case "True/False": q = new Question("True or False: " + cleanSentence(sentence), List.of("True", "False", "-", "-"), 0, "The statement is from the text."); break;
                case "Multiple Choice": q = generateMultipleChoice(sentence, sentences, random); break;
                case "Workout": q = generateWorkout(sentence); break;
            }
            if (q != null) questions.add(q);
        }
        return questions;
    }

    private String cleanSentence(String sentence) {
        // Remove trailing punctuation like ., ?, ! but not if it's part of an abbreviation
        if (sentence.endsWith(".") || sentence.endsWith("?") || sentence.endsWith("!")) {
            return sentence.substring(0, sentence.length() - 1);
        }
        return sentence;
    }

    private Question generateFillInBlank(String sentence) {
        String[] words = sentence.split("\\s+");
        if (words.length < 5) return null;
        
        List<Integer> candidateIndices = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            String w = words[i].replaceAll("[^a-zA-Z]", "");
            if (w.length() > 3 && !isStopWord(w)) {
                candidateIndices.add(i);
            }
        }
        
        if (candidateIndices.isEmpty()) return null;
        
        int indexToHide = candidateIndices.get(new Random().nextInt(candidateIndices.size()));
        String wordToHide = words[indexToHide].replaceAll("[^a-zA-Z]", "");
        
        String questionText = sentence.replaceFirst(Pattern.quote(words[indexToHide]), "_______");
        return new Question("Fill in the blank: " + cleanSentence(questionText), List.of(wordToHide, "is", "the", "not"), 0, "The missing word is " + wordToHide);
    }

    private Question generateMultipleChoice(String sentence, List<String> allSentences, Random random) {
        String[] words = sentence.split("\\s+");
        if (words.length < 4) return null;
        
        String keyWord = "";
        for (int i = words.length - 1; i >= 0; i--) {
            String w = words[i].replaceAll("[^a-zA-Z]", "");
            if (w.length() > 4 && !isStopWord(w)) {
                keyWord = w;
                break;
            }
        }
        
        if (keyWord.isEmpty()) return null;

        String questionText = "What is related to: \"" + cleanSentence(sentence.replace(keyWord, "...")) + "\"?";
        List<String> options = new ArrayList<>(List.of(keyWord));
        
        int attempts = 0;
        while (options.size() < 4 && attempts < 20) {
            attempts++;
            String randomSentence = allSentences.get(random.nextInt(allSentences.size()));
            String[] rWords = randomSentence.split("\\s+");
            if (rWords.length > 0) {
                String distractor = rWords[random.nextInt(rWords.length)].replaceAll("[^a-zA-Z]", "");
                if (distractor.length() > 4 && !options.contains(distractor) && !isStopWord(distractor)) {
                    options.add(distractor);
                }
            }
        }
        
        while (options.size() < 4) {
            options.add("None of the above");
        }
        
        Collections.shuffle(options);
        int correctIndex = options.indexOf(keyWord);
        return new Question(questionText, options, correctIndex, "The correct term is " + keyWord);
    }

    private Question generateWorkout(String sentence) {
        return new Question("Workout / Explain: Analyze the following statement.\n\"" + cleanSentence(sentence) + "\"", List.of("I have analyzed it.", "I need more time.", "Skip", "Next"), 0, "Self-reflection exercise.");
    }
    
    private boolean isStopWord(String word) {
        String w = word.toLowerCase();
        return w.equals("the") || w.equals("and") || w.equals("that") || w.equals("have") || 
               w.equals("for") || w.equals("not") || w.equals("with") || w.equals("this") || 
               w.equals("but") || w.equals("from");
    }
}
