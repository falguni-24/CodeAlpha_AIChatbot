import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
// import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * AI Chatbot - Java Swing GUI with NLP-style preprocessing and rule-based responses.
 * No external libraries or API keys are required.
 */
public class AIChatbot extends JFrame {
    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputField = new JTextField();
    private final JLabel statusLabel = new JLabel("● Online  |  Rule-based NLP chatbot");
    private final ChatbotEngine engine = new ChatbotEngine();

    public AIChatbot() {
        setTitle("Nova AI Chatbot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 600);
        setMinimumSize(new Dimension(620, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(37, 55, 95));
        header.setBorder(new EmptyBorder(18, 22, 16, 22));

        JLabel title = new JLabel("Nova AI Chatbot");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel subtitle = new JLabel("Ask questions. Learn something new.");
        subtitle.setForeground(new Color(220, 228, 245));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subtitle);
        header.add(titleBox, BorderLayout.WEST);

        statusLabel.setForeground(new Color(190, 245, 205));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        header.add(statusLabel, BorderLayout.SOUTH);

        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chatArea.setBackground(new Color(247, 249, 253));
        chatArea.setBorder(new EmptyBorder(15, 16, 15, 16));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(new EmptyBorder(12, 14, 12, 14));
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 213, 226)),
                new EmptyBorder(10, 11, 10, 11)));
        inputField.setToolTipText("Type your message and press Enter");
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(new Color(54, 91, 165));
        sendButton.setFocusPainted(false);
        sendButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        JButton clearButton = new JButton("Clear chat");
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> {
            chatArea.setText("");
            addBotMessage("Chat cleared. What would you like to ask?");
        });

        JPanel actions = new JPanel(new BorderLayout(8, 0));
        actions.setOpaque(false);
        actions.add(clearButton, BorderLayout.WEST);
        actions.add(sendButton, BorderLayout.EAST);
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        addBotMessage("Hello! I'm Nova, your Java chatbot. Ask me about Java, AI, NLP, programming, or type 'help' to see examples.");
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;
        addUserMessage(message);
        inputField.setText("");
        String response = engine.reply(message);
        addBotMessage(response);
    }

    private String timeStamp() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void addUserMessage(String message) {
        chatArea.append("YOU  [" + timeStamp() + "]\n" + message + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void addBotMessage(String message) {
        chatArea.append("NOVA  [" + timeStamp() + "]\n" + message + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AIChatbot().setVisible(true));
    }

    static class ChatbotEngine {
        private final Map<String, String> learnedAnswers = new LinkedHashMap<>();
        private final Map<String, List<String>> intents = new LinkedHashMap<>();

        ChatbotEngine() {
            intents.put("greeting", Arrays.asList("hello", "hi", "hey", "good morning", "good evening"));
            intents.put("java", Arrays.asList("java", "what is java", "java language"));
            intents.put("ai", Arrays.asList("artificial intelligence", "what is ai", " ai "));
            intents.put("nlp", Arrays.asList("nlp", "natural language processing"));
            intents.put("ml", Arrays.asList("machine learning", "what is ml"));
            intents.put("help", Arrays.asList("help", "what can you do", "examples"));
        }

        String reply(String original) {
            String input = normalize(original);
            if (input.isEmpty()) return "Please type a question.";

            // Simple teachable rule: "learn: question = answer"
            if (input.startsWith("learn:")) {
                String body = original.substring(original.indexOf(':') + 1).trim();
                int split = body.indexOf('=');
                if (split > 0 && split < body.length() - 1) {
                    String question = normalize(body.substring(0, split));
                    String answer = body.substring(split + 1).trim();
                    if (!question.isEmpty() && !answer.isEmpty()) {
                        learnedAnswers.put(question, answer);
                        return "Thanks! I learned a new answer for \"" + question + "\" for this session. Try asking it now.";
                    }
                }
                return "Learning format: learn: your question = the answer you want me to give";
            }

            if (input.equals("help") || input.contains("what can you do") || input.contains("examples")) {
                return "You can ask me about Java, AI, NLP, machine learning, or programming. You can also teach me a response with: learn: your question = your answer. This demo remembers taught answers only while the app is running.";
            }

            if (learnedAnswers.containsKey(input)) return learnedAnswers.get(input);

            // NLP-style preprocessing: lowercase, remove punctuation, normalize spaces.
            // Intent matching uses keywords and simple phrase rules rather than a trained ML model.
            if (containsAny(input, "hello", "hi", "hey", "good morning", "good afternoon", "good evening"))
                return "Hello! How can I help you today?";
            if (containsAny(input, "your name", "who are you", "introduce yourself"))
                return "I'm Nova, a Java-based chatbot built with Swing GUI, text normalization, intent matching, and rule-based responses.";
            if (containsAny(input, "thank", "thanks"))
                return "You're welcome! Feel free to ask another question.";
            if (containsAny(input, "bye", "goodbye", "see you"))
                return "Goodbye! Have a great day. Come back whenever you have a question.";
            if (containsAny(input, "what is java", "java language", "java programming", "java"))
                return "Java is a high-level, object-oriented programming language. It is widely used for desktop, web, Android-related, and enterprise applications.";
            if (containsAny(input, "what is ai", "artificial intelligence", " ai "))
                return "Artificial Intelligence (AI) is a field of computing that builds systems able to perform tasks such as recognizing patterns, reasoning, and understanding language.";
            if (containsAny(input, "nlp", "natural language processing"))
                return "Natural Language Processing (NLP) helps computers process human language. This project uses basic NLP-style steps such as lowercasing, punctuation removal, and keyword/intent matching.";
            if (containsAny(input, "machine learning", "what is ml"))
                return "Machine Learning is a branch of AI in which models learn patterns from data. This project demonstrates rule-based logic; it does not train a statistical ML model.";
            if (containsAny(input, "what is programming", "programming"))
                return "Programming means writing instructions that a computer can execute. Java, Python, C, and JavaScript are examples of programming languages.";
            if (containsAny(input, "how are you", "how do you feel"))
                return "I'm working properly and ready to help! What would you like to know?";
            if (containsAny(input, "time", "current time"))
                return "The current local time on this computer is " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".";
            if (containsAny(input, "features", "project points", "what features"))
                return "Project features: Java-based interaction, NLP-style text preprocessing, rule-based answer selection, FAQ responses, a Swing GUI, and session-only custom learning.";
            if (containsAny(input, "how it works", "working"))
                return "Working: (1) user enters text, (2) the program normalizes it, (3) intent/keyword rules are checked, (4) the matching answer is displayed. Custom answers can be taught with the learn command.";
            return "I'm not sure about that yet. Try asking about Java, AI, NLP, machine learning, or type 'help'. You can also teach me using: learn: question = answer";
        }

        private String normalize(String text) {
            return text.toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9\\s:=?]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        private boolean containsAny(String text, String... keywords) {
            for (String keyword : keywords) {
                String k = normalize(keyword);
                if (text.equals(k) || text.contains(k)) return true;
            }
            return false;
        }
    }
}
