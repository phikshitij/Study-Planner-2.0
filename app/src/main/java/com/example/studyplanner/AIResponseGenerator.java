package com.example.studyplanner;

import java.util.HashMap;
import java.util.Map;

public class AIResponseGenerator {
    private static final Map<String, String> knowledgeBase = new HashMap<>();

    static {
        // Software Engineering
        knowledgeBase.put("software engineering", 
            "Software Engineering is the systematic application of engineering principles to design, develop, and maintain software systems. It involves:\n\n" +
            "1. Requirements Analysis\n" +
            "2. Software Design\n" +
            "3. Programming/Implementation\n" +
            "4. Testing & Debugging\n" +
            "5. Software Maintenance\n\n" +
            "Would you like to know more about any specific aspect?");

        // Programming Languages
        knowledgeBase.put("programming languages",
            "Programming languages are formal languages used to write instructions for computers. Popular ones include:\n\n" +
            "1. Python - Great for beginners, AI/ML\n" +
            "2. Java - Enterprise applications, Android\n" +
            "3. JavaScript - Web development\n" +
            "4. C++ - System programming, games\n\n" +
            "Which language would you like to learn more about?");

        // Study Tips
        knowledgeBase.put("study tips",
            "Here are effective study techniques:\n\n" +
            "1. Active Recall - Test yourself\n" +
            "2. Spaced Repetition - Review at intervals\n" +
            "3. Pomodoro Technique - 25min study/5min break\n" +
            "4. Mind Mapping - Visual organization\n" +
            "5. Teaching Others - Reinforces learning\n\n" +
            "Would you like more details about any of these?");

        // Add more knowledge areas as needed
    }

    public static String generateResponse(String userMessage) {
        userMessage = userMessage.toLowerCase().trim();
        
        // Check for greetings
        if (userMessage.matches(".*(hi|hello|hey).*")) {
            return "Hello! I'm your AI study assistant. How can I help you with your studies today?";
        }

        // Check knowledge base
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (userMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Check for specific questions about topics
        if (userMessage.contains("what is") || userMessage.contains("explain") || userMessage.contains("tell me about")) {
            for (String topic : knowledgeBase.keySet()) {
                if (userMessage.contains(topic)) {
                    return knowledgeBase.get(topic);
                }
            }
        }

        // Handle questions about studying
        if (userMessage.contains("how to study") || userMessage.contains("study method")) {
            return knowledgeBase.get("study tips");
        }

        // Handle motivation
        if (userMessage.contains("motivat") || userMessage.contains("inspire")) {
            return "Remember these key points for staying motivated:\n\n" +
                   "1. Set clear, achievable goals\n" +
                   "2. Break large tasks into smaller ones\n" +
                   "3. Celebrate small victories\n" +
                   "4. Visualize your success\n" +
                   "5. Take regular breaks\n\n" +
                   "You've got this! What specific goal are you working towards?";
        }

        // Default response for unclear questions
        if (userMessage.length() < 10) {
            return "Could you please provide more details about what you'd like to know? This will help me give you a more helpful response.";
        }

        return "I understand you're asking about '" + userMessage + "'. Could you please rephrase your question or specify what aspect you'd like to learn about?";
    }
}
