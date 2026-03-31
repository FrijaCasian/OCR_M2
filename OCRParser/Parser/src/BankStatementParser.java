import java.util.*;
import java.util.regex.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BankStatementParser {//metoda principala care primeste textul brut din OCR

    public List<Transaction> parseText(String ocrText) {//lista in care voi salva toate tranzactiile extrase
        List<Transaction> transactions = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//formator care transforma datele din string in localdate

        String[] lines = ocrText.split("\\r?\\n");//impart textul in linii,fiecare linie  poate contine o tranzactie

        for (String line : lines) {//parcurg fiecare linie
            line = line.trim();

            // ignor liniile inutile
            if (line.isEmpty() || !line.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*")) {
                continue;
            }
„
            try {
                // extrag data
                String dateStr = line.substring(0, 10);
                LocalDate date = LocalDate.parse(dateStr, formatter);

                // extrag suma
                Matcher matcher = Pattern.compile("(\\d+[.,]?\\d*)\\s*$").matcher(line);
                double amount = 0;

                if (matcher.find()) {//inlocuiesc , in .
                    amount = Double.parseDouble(matcher.group(1).replace(",", "."));
                }

                // extragem descrierea
                String description = line
                        .substring(10, line.lastIndexOf(matcher.group(1)))
                        .trim();

                // creăm obiectul
                transactions.add(new Transaction(date, description, amount));

            } catch (Exception e) {
                // ignorăm liniile care nu pot fi parse-uite
            }
        }

        return transactions;//returnez lista de tranzactii
    }
}